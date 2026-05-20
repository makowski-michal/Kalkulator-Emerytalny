package pl.edu.payroll.service;

import org.springframework.stereotype.Service;
import pl.edu.payroll.dto.PayslipRequestDto;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.entity.Payslip;
import pl.edu.payroll.repository.AbsenceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class PayrollService {

    // Stawki ZUS 2025 – pracownik
    private static final BigDecimal PENSION_RATE_EMPLOYEE    = new BigDecimal("0.0976");
    private static final BigDecimal DISABILITY_RATE_EMPLOYEE = new BigDecimal("0.0150");
    private static final BigDecimal SICKNESS_RATE            = new BigDecimal("0.0245");
    private static final BigDecimal HEALTH_RATE              = new BigDecimal("0.0900");
    private static final BigDecimal HEALTH_DEDUCTIBLE_RATE   = new BigDecimal("0.0775");

    // Stawki ZUS 2025 – pracodawca
    private static final BigDecimal PENSION_RATE_EMPLOYER    = new BigDecimal("0.0976");
    private static final BigDecimal DISABILITY_RATE_EMPLOYER = new BigDecimal("0.0650");
    private static final BigDecimal ACCIDENT_RATE            = new BigDecimal("0.0167");
    private static final BigDecimal LABOR_FUND_RATE          = new BigDecimal("0.0245");
    private static final BigDecimal FGSP_RATE                = new BigDecimal("0.0010");

    // Koszty uzyskania przychodu
    private static final BigDecimal KUP                      = new BigDecimal("250");
    // Ulga podatkowa miesięczna (PIT-2)
    private static final BigDecimal TAX_RELIEF_MONTHLY       = new BigDecimal("300");
    // Progi PIT (roczne)
    private static final BigDecimal PIT_THRESHOLD_ANNUAL     = new BigDecimal("120000");
    private static final BigDecimal PIT_RATE_LOW             = new BigDecimal("0.12");
    private static final BigDecimal PIT_RATE_HIGH            = new BigDecimal("0.32");

    private final AbsenceRepository absenceRepository;

    public PayrollService(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    public Payslip calculate(Employee employee, PayslipRequestDto req) {
        int year = req.getPeriodYear();
        int month = req.getPeriodMonth();

        // Pobierz nieobecności z bazy
        int sickLeaveDays = absenceRepository.sumSickLeaveDays(employee.getId(), year, month);
        int unpaidLeaveDays = absenceRepository.sumUnpaidLeaveDays(employee.getId(), year, month);

        int workingDays = workingDaysInMonth(year, month);

        // KROK 1: Brutto bazowe
        BigDecimal base = employee.getGrossSalary()
            .add(nvl(req.getBonus()))
            .add(nvl(req.getAllowances()));

        if (unpaidLeaveDays > 0 && workingDays > 0) {
            BigDecimal dailyRate = employee.getGrossSalary()
                .divide(BigDecimal.valueOf(workingDays), 10, RoundingMode.HALF_UP);
            base = base.subtract(dailyRate.multiply(BigDecimal.valueOf(unpaidLeaveDays)));
        }

        if (sickLeaveDays > 0) {
            // Wynagrodzenie chorobowe = 80% stawki dziennej (1/30 brutto)
            BigDecimal sickDailyFull = employee.getGrossSalary()
                .divide(BigDecimal.valueOf(30), 10, RoundingMode.HALF_UP);
            BigDecimal sickPay = sickDailyFull
                .multiply(new BigDecimal("0.80"))
                .multiply(BigDecimal.valueOf(sickLeaveDays));
            BigDecimal sickDeduction = sickDailyFull.multiply(BigDecimal.valueOf(sickLeaveDays));
            base = base.subtract(sickDeduction).add(sickPay);
        }
        base = base.setScale(2, RoundingMode.HALF_UP);

        // KROK 2: Składki społeczne pracownika
        BigDecimal pensionEmployee    = base.multiply(PENSION_RATE_EMPLOYEE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal disabilityEmployee = base.multiply(DISABILITY_RATE_EMPLOYEE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sicknessBase       = (sickLeaveDays > 0)
            ? employee.getGrossSalary()
                .subtract(employee.getGrossSalary()
                    .divide(BigDecimal.valueOf(30), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(sickLeaveDays)))
                .setScale(2, RoundingMode.HALF_UP)
            : base;
        BigDecimal sicknessContrib    = sicknessBase.multiply(SICKNESS_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal socialTotal        = pensionEmployee.add(disabilityEmployee).add(sicknessContrib);

        // KROK 3: Składka zdrowotna
        BigDecimal healthBase   = base.subtract(socialTotal);
        BigDecimal healthContrib = healthBase.multiply(HEALTH_RATE).setScale(2, RoundingMode.HALF_UP);

        // KROK 4: Zaliczka PIT
        BigDecimal taxBase = base.subtract(socialTotal).subtract(KUP);
        taxBase = taxBase.setScale(0, RoundingMode.HALF_UP);
        if (taxBase.compareTo(BigDecimal.ZERO) < 0) taxBase = BigDecimal.ZERO;

        BigDecimal annualTaxBase = taxBase.multiply(BigDecimal.valueOf(12));
        BigDecimal pitRate = annualTaxBase.compareTo(PIT_THRESHOLD_ANNUAL) <= 0
            ? PIT_RATE_LOW : PIT_RATE_HIGH;
        BigDecimal taxBeforeRelief = taxBase.multiply(pitRate);
        if (employee.isTaxRelief()) {
            taxBeforeRelief = taxBeforeRelief.subtract(TAX_RELIEF_MONTHLY);
        }
        BigDecimal healthDeductible = healthBase.multiply(HEALTH_DEDUCTIBLE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal incomeTaxAdvance = taxBeforeRelief.subtract(healthDeductible);
        incomeTaxAdvance = incomeTaxAdvance.setScale(0, RoundingMode.HALF_UP);
        incomeTaxAdvance = incomeTaxAdvance.max(BigDecimal.ZERO);

        // KROK 5: Netto
        BigDecimal netSalary = base
            .subtract(socialTotal)
            .subtract(healthContrib)
            .subtract(incomeTaxAdvance)
            .subtract(nvl(req.getGarnishment()))
            .subtract(nvl(req.getVoluntaryDeduction()))
            .setScale(2, RoundingMode.HALF_UP);

        // KROK 6: Koszt pracodawcy
        BigDecimal employerTotalCost = base
            .add(base.multiply(PENSION_RATE_EMPLOYER))
            .add(base.multiply(DISABILITY_RATE_EMPLOYER))
            .add(base.multiply(ACCIDENT_RATE))
            .add(base.multiply(LABOR_FUND_RATE))
            .add(base.multiply(FGSP_RATE))
            .setScale(2, RoundingMode.HALF_UP);

        Payslip p = new Payslip();
        p.setEmployee(employee);
        p.setPeriodYear(year);
        p.setPeriodMonth(month);
        p.setDocumentDate(req.getDocumentDate() != null ? req.getDocumentDate() : LocalDate.now());
        p.setGrossSalary(base);
        p.setBonus(nvl(req.getBonus()));
        p.setAllowances(nvl(req.getAllowances()));
        p.setSickLeaveDays(sickLeaveDays);
        p.setUnpaidLeaveDays(unpaidLeaveDays);
        p.setGarnishment(nvl(req.getGarnishment()));
        p.setVoluntaryDeduction(nvl(req.getVoluntaryDeduction()));
        p.setPensionContribEmployee(pensionEmployee);
        p.setDisabilityContribEmployee(disabilityEmployee);
        p.setSicknessContrib(sicknessContrib);
        p.setHealthContrib(healthContrib);
        p.setIncomeTaxAdvance(incomeTaxAdvance);
        p.setNetSalary(netSalary);
        p.setEmployerTotalCost(employerTotalCost);

        return p;
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static int workingDaysInMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int days = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate date = LocalDate.of(year, month, d);
            int dow = date.getDayOfWeek().getValue();
            if (dow <= 5) days++;
        }
        return days;
    }
}

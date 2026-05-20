package pl.edu.payroll.service;

import org.springframework.stereotype.Service;
import pl.edu.payroll.dto.RetirementForecastDto;
import pl.edu.payroll.dto.RetirementForecastResponseDto;
import pl.edu.payroll.dto.RetirementResponseDto;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.repository.GusRepository;
import pl.edu.payroll.repository.PayslipRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class RetirementService {

    private static final BigDecimal PENSION_RATE_EMPLOYEE = new BigDecimal("0.0976");

    private final PayslipRepository payslipRepository;
    private final GusRepository gusRepository;

    public RetirementService(PayslipRepository payslipRepository, GusRepository gusRepository) {
        this.payslipRepository = payslipRepository;
        this.gusRepository = gusRepository;
    }

    public RetirementResponseDto getCurrent(Employee employee) {
        BigDecimal sumContribs = payslipRepository.sumPensionContribByEmployee(employee.getId());
        int currentAge = Period.between(employee.getBirthDate(), LocalDate.now()).getYears();
        String gender = genderFromPesel(employee.getPesel());

        BigDecimal gusMonths = gusRepository
            .findLatestByAgeAndGender(currentAge, gender)
            .map(g -> g.getMonthsRemaining())
            .orElse(new BigDecimal("200"));

        BigDecimal currentPension = gusMonths.compareTo(BigDecimal.ZERO) > 0
            ? sumContribs.divide(gusMonths, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Dane do wykresu – skumulowane składki miesięcznie
        List<Object[]> raw = payslipRepository.monthlyPensionContribs(employee.getId());
        List<RetirementResponseDto.MonthlyContribution> monthly = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (Object[] row : raw) {
            int yr = ((Number) row[0]).intValue();
            int mo = ((Number) row[1]).intValue();
            BigDecimal contrib = (BigDecimal) row[2];
            cumulative = cumulative.add(contrib);
            monthly.add(new RetirementResponseDto.MonthlyContribution(yr, mo,
                cumulative.setScale(2, RoundingMode.HALF_UP)));
        }

        RetirementResponseDto dto = new RetirementResponseDto();
        dto.setCurrentPension(currentPension);
        dto.setTotalPensionContribs(sumContribs.setScale(2, RoundingMode.HALF_UP));
        dto.setCurrentAge(currentAge);
        dto.setMonthlyContributions(monthly);
        return dto;
    }

    public RetirementForecastResponseDto forecast(Employee employee, RetirementForecastDto req) {
        BigDecimal sumContribs = payslipRepository.sumPensionContribByEmployee(employee.getId());
        int currentAge = Period.between(employee.getBirthDate(), LocalDate.now()).getYears();
        int retirementAge = req.getTargetRetirementAge() > 0
            ? req.getTargetRetirementAge() : employee.getRetirementAge();

        int monthsToRetirement = Math.max(0, (retirementAge - currentAge) * 12);
        BigDecimal futureContribs = employee.getGrossSalary()
            .multiply(PENSION_RATE_EMPLOYEE)
            .multiply(BigDecimal.valueOf(monthsToRetirement))
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal ofe = req.getOfeAmount() != null ? req.getOfeAmount() : BigDecimal.ZERO;
        BigDecimal totalContribs = sumContribs.add(futureContribs).add(ofe);

        String gender = genderFromPesel(employee.getPesel());
        BigDecimal gusAtRetirement = gusRepository
            .findLatestByAgeAndGender(retirementAge, gender)
            .map(g -> g.getMonthsRemaining())
            .orElse(new BigDecimal("180"));

        BigDecimal futurePension = gusAtRetirement.compareTo(BigDecimal.ZERO) > 0
            ? totalContribs.divide(gusAtRetirement, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        RetirementForecastResponseDto dto = new RetirementForecastResponseDto();
        dto.setFuturePension(futurePension);
        dto.setTotalContribs(totalContribs.setScale(2, RoundingMode.HALF_UP));
        dto.setTargetRetirementAge(retirementAge);
        dto.setMonthsToRetirement(monthsToRetirement);
        return dto;
    }

    private String genderFromPesel(String pesel) {
        if (pesel == null || pesel.length() < 10) return "M";
        int genderDigit = Character.getNumericValue(pesel.charAt(9));
        return (genderDigit % 2 == 0) ? "F" : "M";
    }
}

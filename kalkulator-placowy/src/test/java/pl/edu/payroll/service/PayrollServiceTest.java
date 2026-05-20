package pl.edu.payroll.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.edu.payroll.dto.PayslipRequestDto;
import pl.edu.payroll.entity.Company;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.entity.Payslip;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.AbsenceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class PayrollServiceTest {

    private PayrollService payrollService;
    private AbsenceRepository absenceRepository;

    @BeforeEach
    void setUp() {
        absenceRepository = Mockito.mock(AbsenceRepository.class);
        payrollService = new PayrollService(absenceRepository);
        Mockito.when(absenceRepository.sumSickLeaveDays(anyLong(), anyInt(), anyInt())).thenReturn(0);
        Mockito.when(absenceRepository.sumUnpaidLeaveDays(anyLong(), anyInt(), anyInt())).thenReturn(0);
    }

    private Employee createEmployee(BigDecimal grossSalary, boolean taxRelief) {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.pl");
        user.setRole("EMPLOYEE");

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Firma");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setUser(user);
        employee.setCompany(company);
        employee.setGrossSalary(grossSalary);
        employee.setTaxRelief(taxRelief);
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        return employee;
    }

    private PayslipRequestDto createRequest(int year, int month) {
        PayslipRequestDto req = new PayslipRequestDto();
        req.setEmployeeId(1L);
        req.setPeriodYear(year);
        req.setPeriodMonth(month);
        req.setBonus(BigDecimal.ZERO);
        req.setAllowances(BigDecimal.ZERO);
        req.setGarnishment(BigDecimal.ZERO);
        req.setVoluntaryDeduction(BigDecimal.ZERO);
        return req;
    }

    @Test
    void calculateNetto_basicSalary_shouldBePositive() {
        Employee emp = createEmployee(new BigDecimal("5000"), true);
        PayslipRequestDto req = createRequest(2025, 1);

        Payslip payslip = payrollService.calculate(emp, req);

        assertTrue(payslip.getNetSalary().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(payslip.getNetSalary().compareTo(payslip.getGrossSalary()) < 0);
    }

    @Test
    void calculateNetto_noTaxRelief_shouldBeLowerThanWithRelief() {
        Employee empWithRelief = createEmployee(new BigDecimal("6000"), true);
        Employee empNoRelief = createEmployee(new BigDecimal("6000"), false);
        PayslipRequestDto req1 = createRequest(2025, 3);
        PayslipRequestDto req2 = createRequest(2025, 3);

        Payslip withRelief = payrollService.calculate(empWithRelief, req1);
        Payslip noRelief = payrollService.calculate(empNoRelief, req2);

        assertTrue(withRelief.getNetSalary().compareTo(noRelief.getNetSalary()) > 0,
            "Ulga podatkowa powinna zwiększyć netto");
    }

    @Test
    void calculateNetto_sickLeave_shouldReduceSicknessContrib() {
        Mockito.when(absenceRepository.sumSickLeaveDays(anyLong(), anyInt(), anyInt())).thenReturn(5);
        Employee emp = createEmployee(new BigDecimal("5000"), true);
        PayslipRequestDto req = createRequest(2025, 2);

        Payslip payslip = payrollService.calculate(emp, req);

        assertEquals(5, payslip.getSickLeaveDays());
        // Składka chorobowa powinna być mniejsza przy L4
        assertTrue(payslip.getSicknessContrib().compareTo(
            new BigDecimal("5000").multiply(new BigDecimal("0.0245"))) < 0);
    }

    @Test
    void calculateEmployerCost_shouldBeHigherThanGross() {
        Employee emp = createEmployee(new BigDecimal("5000"), true);
        PayslipRequestDto req = createRequest(2025, 4);

        Payslip payslip = payrollService.calculate(emp, req);

        assertTrue(payslip.getEmployerTotalCost().compareTo(payslip.getGrossSalary()) > 0);
    }

    @Test
    void calculateNetto_withGarnishment_shouldReduceNet() {
        Employee emp = createEmployee(new BigDecimal("5000"), true);
        PayslipRequestDto req = createRequest(2025, 5);
        req.setGarnishment(new BigDecimal("500"));

        Payslip withGarnishment = payrollService.calculate(emp, req);

        PayslipRequestDto req2 = createRequest(2025, 5);
        Payslip withoutGarnishment = payrollService.calculate(emp, req2);

        assertEquals(0, withGarnishment.getNetSalary()
            .compareTo(withoutGarnishment.getNetSalary().subtract(new BigDecimal("500"))));
    }

    @Test
    void calculateNetto_pensionContrib_shouldBe976Percent() {
        Employee emp = createEmployee(new BigDecimal("10000"), true);
        PayslipRequestDto req = createRequest(2025, 6);

        Payslip payslip = payrollService.calculate(emp, req);

        BigDecimal expected = new BigDecimal("10000").multiply(new BigDecimal("0.0976"))
            .setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(0, expected.compareTo(payslip.getPensionContribEmployee()),
            "Składka emerytalna powinna wynosić 9,76% brutto");
    }
}

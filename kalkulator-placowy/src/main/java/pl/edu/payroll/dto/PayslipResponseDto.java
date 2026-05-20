package pl.edu.payroll.dto;

import pl.edu.payroll.entity.Payslip;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayslipResponseDto {

    private Long id;
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeePesel;
    private String companyName;
    private String companyNip;
    private String companyRegon;
    private int periodYear;
    private int periodMonth;
    private LocalDate documentDate;
    private BigDecimal grossSalary;
    private BigDecimal bonus;
    private BigDecimal allowances;
    private int sickLeaveDays;
    private int unpaidLeaveDays;
    private BigDecimal garnishment;
    private BigDecimal voluntaryDeduction;
    private BigDecimal pensionContribEmployee;
    private BigDecimal disabilityContribEmployee;
    private BigDecimal sicknessContrib;
    private BigDecimal healthContrib;
    private BigDecimal incomeTaxAdvance;
    private BigDecimal netSalary;
    private BigDecimal employerTotalCost;

    public static PayslipResponseDto from(Payslip p) {
        PayslipResponseDto dto = new PayslipResponseDto();
        dto.id = p.getId();
        dto.employeeId = p.getEmployee().getId();
        dto.employeeFirstName = p.getEmployee().getFirstName();
        dto.employeeLastName = p.getEmployee().getLastName();
        dto.employeePesel = p.getEmployee().getPesel();
        dto.companyName = p.getEmployee().getCompany().getName();
        dto.companyNip = p.getEmployee().getCompany().getNip();
        dto.companyRegon = p.getEmployee().getCompany().getRegon();
        dto.periodYear = p.getPeriodYear();
        dto.periodMonth = p.getPeriodMonth();
        dto.documentDate = p.getDocumentDate();
        dto.grossSalary = p.getGrossSalary();
        dto.bonus = p.getBonus();
        dto.allowances = p.getAllowances();
        dto.sickLeaveDays = p.getSickLeaveDays();
        dto.unpaidLeaveDays = p.getUnpaidLeaveDays();
        dto.garnishment = p.getGarnishment();
        dto.voluntaryDeduction = p.getVoluntaryDeduction();
        dto.pensionContribEmployee = p.getPensionContribEmployee();
        dto.disabilityContribEmployee = p.getDisabilityContribEmployee();
        dto.sicknessContrib = p.getSicknessContrib();
        dto.healthContrib = p.getHealthContrib();
        dto.incomeTaxAdvance = p.getIncomeTaxAdvance();
        dto.netSalary = p.getNetSalary();
        dto.employerTotalCost = p.getEmployerTotalCost();
        return dto;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeFirstName() { return employeeFirstName; }
    public String getEmployeeLastName() { return employeeLastName; }
    public String getEmployeePesel() { return employeePesel; }
    public String getCompanyName() { return companyName; }
    public String getCompanyNip() { return companyNip; }
    public String getCompanyRegon() { return companyRegon; }
    public int getPeriodYear() { return periodYear; }
    public int getPeriodMonth() { return periodMonth; }
    public LocalDate getDocumentDate() { return documentDate; }
    public BigDecimal getGrossSalary() { return grossSalary; }
    public BigDecimal getBonus() { return bonus; }
    public BigDecimal getAllowances() { return allowances; }
    public int getSickLeaveDays() { return sickLeaveDays; }
    public int getUnpaidLeaveDays() { return unpaidLeaveDays; }
    public BigDecimal getGarnishment() { return garnishment; }
    public BigDecimal getVoluntaryDeduction() { return voluntaryDeduction; }
    public BigDecimal getPensionContribEmployee() { return pensionContribEmployee; }
    public BigDecimal getDisabilityContribEmployee() { return disabilityContribEmployee; }
    public BigDecimal getSicknessContrib() { return sicknessContrib; }
    public BigDecimal getHealthContrib() { return healthContrib; }
    public BigDecimal getIncomeTaxAdvance() { return incomeTaxAdvance; }
    public BigDecimal getNetSalary() { return netSalary; }
    public BigDecimal getEmployerTotalCost() { return employerTotalCost; }
}

package pl.edu.payroll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payslips",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id","period_year","period_month"}))
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "period_month", nullable = false)
    private int periodMonth;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "gross_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "sick_leave_days")
    private int sickLeaveDays = 0;

    @Column(name = "unpaid_leave_days")
    private int unpaidLeaveDays = 0;

    @Column(precision = 12, scale = 2)
    private BigDecimal garnishment = BigDecimal.ZERO;

    @Column(name = "voluntary_deduction", precision = 12, scale = 2)
    private BigDecimal voluntaryDeduction = BigDecimal.ZERO;

    @Column(name = "pension_contrib_employee", nullable = false, precision = 12, scale = 2)
    private BigDecimal pensionContribEmployee;

    @Column(name = "disability_contrib_employee", nullable = false, precision = 12, scale = 2)
    private BigDecimal disabilityContribEmployee;

    @Column(name = "sickness_contrib", nullable = false, precision = 12, scale = 2)
    private BigDecimal sicknessContrib;

    @Column(name = "health_contrib", nullable = false, precision = 12, scale = 2)
    private BigDecimal healthContrib;

    @Column(name = "income_tax_advance", nullable = false, precision = 12, scale = 2)
    private BigDecimal incomeTaxAdvance;

    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "employer_total_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal employerTotalCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public int getPeriodYear() { return periodYear; }
    public void setPeriodYear(int periodYear) { this.periodYear = periodYear; }

    public int getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(int periodMonth) { this.periodMonth = periodMonth; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }

    public int getSickLeaveDays() { return sickLeaveDays; }
    public void setSickLeaveDays(int sickLeaveDays) { this.sickLeaveDays = sickLeaveDays; }

    public int getUnpaidLeaveDays() { return unpaidLeaveDays; }
    public void setUnpaidLeaveDays(int unpaidLeaveDays) { this.unpaidLeaveDays = unpaidLeaveDays; }

    public BigDecimal getGarnishment() { return garnishment; }
    public void setGarnishment(BigDecimal garnishment) { this.garnishment = garnishment; }

    public BigDecimal getVoluntaryDeduction() { return voluntaryDeduction; }
    public void setVoluntaryDeduction(BigDecimal voluntaryDeduction) { this.voluntaryDeduction = voluntaryDeduction; }

    public BigDecimal getPensionContribEmployee() { return pensionContribEmployee; }
    public void setPensionContribEmployee(BigDecimal pensionContribEmployee) { this.pensionContribEmployee = pensionContribEmployee; }

    public BigDecimal getDisabilityContribEmployee() { return disabilityContribEmployee; }
    public void setDisabilityContribEmployee(BigDecimal disabilityContribEmployee) { this.disabilityContribEmployee = disabilityContribEmployee; }

    public BigDecimal getSicknessContrib() { return sicknessContrib; }
    public void setSicknessContrib(BigDecimal sicknessContrib) { this.sicknessContrib = sicknessContrib; }

    public BigDecimal getHealthContrib() { return healthContrib; }
    public void setHealthContrib(BigDecimal healthContrib) { this.healthContrib = healthContrib; }

    public BigDecimal getIncomeTaxAdvance() { return incomeTaxAdvance; }
    public void setIncomeTaxAdvance(BigDecimal incomeTaxAdvance) { this.incomeTaxAdvance = incomeTaxAdvance; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public BigDecimal getEmployerTotalCost() { return employerTotalCost; }
    public void setEmployerTotalCost(BigDecimal employerTotalCost) { this.employerTotalCost = employerTotalCost; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

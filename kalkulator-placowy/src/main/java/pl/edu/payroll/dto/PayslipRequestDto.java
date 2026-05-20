package pl.edu.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PayslipRequestDto {

    @NotNull
    private Long employeeId;

    @NotNull
    @Min(2000)
    private Integer periodYear;

    @NotNull
    @Min(1) @Max(12)
    private Integer periodMonth;

    private LocalDate documentDate;

    private BigDecimal bonus = BigDecimal.ZERO;
    private BigDecimal allowances = BigDecimal.ZERO;
    private BigDecimal garnishment = BigDecimal.ZERO;
    private BigDecimal voluntaryDeduction = BigDecimal.ZERO;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Integer getPeriodYear() { return periodYear; }
    public void setPeriodYear(Integer periodYear) { this.periodYear = periodYear; }

    public Integer getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(Integer periodMonth) { this.periodMonth = periodMonth; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }

    public BigDecimal getGarnishment() { return garnishment; }
    public void setGarnishment(BigDecimal garnishment) { this.garnishment = garnishment; }

    public BigDecimal getVoluntaryDeduction() { return voluntaryDeduction; }
    public void setVoluntaryDeduction(BigDecimal voluntaryDeduction) { this.voluntaryDeduction = voluntaryDeduction; }
}

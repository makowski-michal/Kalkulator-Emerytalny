package pl.edu.payroll.dto;

import java.math.BigDecimal;

public class RetirementForecastResponseDto {

    private BigDecimal futurePension;
    private BigDecimal totalContribs;
    private int targetRetirementAge;
    private int monthsToRetirement;

    public BigDecimal getFuturePension() { return futurePension; }
    public void setFuturePension(BigDecimal futurePension) { this.futurePension = futurePension; }

    public BigDecimal getTotalContribs() { return totalContribs; }
    public void setTotalContribs(BigDecimal totalContribs) { this.totalContribs = totalContribs; }

    public int getTargetRetirementAge() { return targetRetirementAge; }
    public void setTargetRetirementAge(int targetRetirementAge) { this.targetRetirementAge = targetRetirementAge; }

    public int getMonthsToRetirement() { return monthsToRetirement; }
    public void setMonthsToRetirement(int monthsToRetirement) { this.monthsToRetirement = monthsToRetirement; }
}

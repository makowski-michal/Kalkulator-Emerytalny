package pl.edu.payroll.dto;

import java.math.BigDecimal;

public class RetirementForecastDto {

    private int targetRetirementAge;
    private BigDecimal ofeAmount;

    public int getTargetRetirementAge() { return targetRetirementAge; }
    public void setTargetRetirementAge(int targetRetirementAge) { this.targetRetirementAge = targetRetirementAge; }

    public BigDecimal getOfeAmount() { return ofeAmount; }
    public void setOfeAmount(BigDecimal ofeAmount) { this.ofeAmount = ofeAmount; }
}

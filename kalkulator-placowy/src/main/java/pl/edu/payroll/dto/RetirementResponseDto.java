package pl.edu.payroll.dto;

import java.math.BigDecimal;
import java.util.List;

public class RetirementResponseDto {

    private BigDecimal currentPension;
    private BigDecimal totalPensionContribs;
    private int currentAge;
    private List<MonthlyContribution> monthlyContributions;

    public static class MonthlyContribution {
        private int year;
        private int month;
        private BigDecimal cumulative;

        public MonthlyContribution(int year, int month, BigDecimal cumulative) {
            this.year = year;
            this.month = month;
            this.cumulative = cumulative;
        }

        public int getYear() { return year; }
        public int getMonth() { return month; }
        public BigDecimal getCumulative() { return cumulative; }
    }

    public BigDecimal getCurrentPension() { return currentPension; }
    public void setCurrentPension(BigDecimal currentPension) { this.currentPension = currentPension; }

    public BigDecimal getTotalPensionContribs() { return totalPensionContribs; }
    public void setTotalPensionContribs(BigDecimal totalPensionContribs) { this.totalPensionContribs = totalPensionContribs; }

    public int getCurrentAge() { return currentAge; }
    public void setCurrentAge(int currentAge) { this.currentAge = currentAge; }

    public List<MonthlyContribution> getMonthlyContributions() { return monthlyContributions; }
    public void setMonthlyContributions(List<MonthlyContribution> monthlyContributions) { this.monthlyContributions = monthlyContributions; }
}

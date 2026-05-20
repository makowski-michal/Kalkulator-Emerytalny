package pl.edu.payroll.dto;

import java.math.BigDecimal;

public class EmployeeUpdateDto {

    private String firstName;
    private String lastName;
    private String pesel;
    private String address;
    private String zusTitleCode;
    private BigDecimal grossSalary;
    private Boolean taxRelief;
    private Integer retirementAge;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getZusTitleCode() { return zusTitleCode; }
    public void setZusTitleCode(String zusTitleCode) { this.zusTitleCode = zusTitleCode; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public Boolean getTaxRelief() { return taxRelief; }
    public void setTaxRelief(Boolean taxRelief) { this.taxRelief = taxRelief; }

    public Integer getRetirementAge() { return retirementAge; }
    public void setRetirementAge(Integer retirementAge) { this.retirementAge = retirementAge; }
}

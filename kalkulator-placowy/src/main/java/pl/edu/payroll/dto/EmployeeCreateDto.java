package pl.edu.payroll.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeCreateDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String tempPassword;

    private String pesel;
    private String address;

    @NotNull
    private LocalDate birthDate;

    private int retirementAge = 65;
    private String zusTitleCode;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal grossSalary;

    private boolean taxRelief = true;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTempPassword() { return tempPassword; }
    public void setTempPassword(String tempPassword) { this.tempPassword = tempPassword; }

    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public int getRetirementAge() { return retirementAge; }
    public void setRetirementAge(int retirementAge) { this.retirementAge = retirementAge; }

    public String getZusTitleCode() { return zusTitleCode; }
    public void setZusTitleCode(String zusTitleCode) { this.zusTitleCode = zusTitleCode; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public boolean isTaxRelief() { return taxRelief; }
    public void setTaxRelief(boolean taxRelief) { this.taxRelief = taxRelief; }
}

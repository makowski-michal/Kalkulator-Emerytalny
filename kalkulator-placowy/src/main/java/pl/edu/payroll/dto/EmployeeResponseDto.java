package pl.edu.payroll.dto;

import pl.edu.payroll.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeResponseDto {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String pesel;
    private String address;
    private LocalDate birthDate;
    private int retirementAge;
    private String zusTitleCode;
    private BigDecimal grossSalary;
    private boolean taxRelief;
    private String email;
    private Long companyId;
    private String companyName;

    public static EmployeeResponseDto from(Employee e) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.id = e.getId();
        dto.userId = e.getUser().getId();
        dto.firstName = e.getFirstName();
        dto.lastName = e.getLastName();
        dto.pesel = e.getPesel();
        dto.address = e.getAddress();
        dto.birthDate = e.getBirthDate();
        dto.retirementAge = e.getRetirementAge();
        dto.zusTitleCode = e.getZusTitleCode();
        dto.grossSalary = e.getGrossSalary();
        dto.taxRelief = e.isTaxRelief();
        dto.email = e.getUser().getEmail();
        dto.companyId = e.getCompany().getId();
        dto.companyName = e.getCompany().getName();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPesel() { return pesel; }
    public String getAddress() { return address; }
    public LocalDate getBirthDate() { return birthDate; }
    public int getRetirementAge() { return retirementAge; }
    public String getZusTitleCode() { return zusTitleCode; }
    public BigDecimal getGrossSalary() { return grossSalary; }
    public boolean isTaxRelief() { return taxRelief; }
    public String getEmail() { return email; }
    public Long getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
}

package pl.edu.payroll.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.payroll.dto.EmployeeCreateDto;
import pl.edu.payroll.dto.EmployeeResponseDto;
import pl.edu.payroll.dto.EmployeeUpdateDto;
import pl.edu.payroll.entity.Company;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.CompanyRepository;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EmployeeResponseDto create(EmployeeCreateDto dto, Long employerUserId) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email już istnieje: " + dto.getEmail());
        }

        Company company = companyRepository.findByOwnerId(employerUserId)
            .orElseThrow(() -> new IllegalStateException("Pracodawca nie ma przypisanej firmy"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getTempPassword()));
        user.setRole("EMPLOYEE");
        userRepository.save(user);

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setCompany(company);
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPesel(dto.getPesel());
        employee.setAddress(dto.getAddress());
        employee.setBirthDate(dto.getBirthDate());
        employee.setRetirementAge(dto.getRetirementAge());
        employee.setZusTitleCode(dto.getZusTitleCode());
        employee.setGrossSalary(dto.getGrossSalary());
        employee.setTaxRelief(dto.isTaxRelief());
        employeeRepository.save(employee);

        return EmployeeResponseDto.from(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> listForEmployer(Long employerUserId, String search) {
        Company company = companyRepository.findByOwnerId(employerUserId)
            .orElseThrow(() -> new IllegalStateException("Pracodawca nie ma przypisanej firmy"));

        List<Employee> employees = (search != null && !search.isBlank())
            ? employeeRepository.searchByCompany(company.getId(), search)
            : employeeRepository.findByCompanyId(company.getId());

        return employees.stream().map(EmployeeResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getById(Long employeeId, Long requestingUserId, String requestingRole) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));

        if ("EMPLOYER".equals(requestingRole)) {
            return EmployeeResponseDto.from(employee);
        }
        // EMPLOYEE can only see own data
        if (!employee.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Brak dostępu");
        }
        return EmployeeResponseDto.from(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
            .map(EmployeeResponseDto::from)
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie znaleziony"));
    }

    public EmployeeResponseDto update(Long employeeId, EmployeeUpdateDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));

        if (dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) employee.setLastName(dto.getLastName());
        if (dto.getPesel() != null) employee.setPesel(dto.getPesel());
        if (dto.getAddress() != null) employee.setAddress(dto.getAddress());
        if (dto.getZusTitleCode() != null) employee.setZusTitleCode(dto.getZusTitleCode());
        if (dto.getGrossSalary() != null) employee.setGrossSalary(dto.getGrossSalary());
        if (dto.getTaxRelief() != null) employee.setTaxRelief(dto.getTaxRelief());
        if (dto.getRetirementAge() != null) employee.setRetirementAge(dto.getRetirementAge());

        return EmployeeResponseDto.from(employeeRepository.save(employee));
    }

    public void deactivate(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));
        employee.getUser().setActive(false);
        userRepository.save(employee.getUser());
    }
}

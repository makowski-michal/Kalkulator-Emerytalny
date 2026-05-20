package pl.edu.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.payroll.dto.RetirementForecastDto;
import pl.edu.payroll.dto.RetirementForecastResponseDto;
import pl.edu.payroll.dto.RetirementResponseDto;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.UserRepository;
import pl.edu.payroll.service.RetirementService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/retirement")
public class RetirementController {

    private final RetirementService retirementService;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public RetirementController(RetirementService retirementService,
                                 EmployeeRepository employeeRepository,
                                 UserRepository userRepository) {
        this.retirementService = retirementService;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{employeeId}/current")
    public ResponseEntity<?> getCurrent(@PathVariable Long employeeId, Authentication auth) {
        try {
            Employee employee = resolveEmployee(employeeId, auth);
            RetirementResponseDto dto = retirementService.getCurrent(employee);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{employeeId}/forecast")
    public ResponseEntity<?> forecast(@PathVariable Long employeeId,
                                       @RequestBody RetirementForecastDto req,
                                       Authentication auth) {
        try {
            Employee employee = resolveEmployee(employeeId, auth);
            // OFE amount NOT saved – used only in-memory (WNF.04)
            RetirementForecastResponseDto dto = retirementService.forecast(employee, req);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Employee resolveEmployee(Long employeeId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        String role = auth.getAuthorities().stream().findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", "")).orElse("EMPLOYEE");

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));

        if ("EMPLOYER".equals(role)) return employee;

        if (!employee.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Brak dostępu");
        }
        return employee;
    }
}

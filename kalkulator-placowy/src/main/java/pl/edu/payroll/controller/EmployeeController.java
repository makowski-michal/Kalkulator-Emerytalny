package pl.edu.payroll.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.payroll.dto.EmployeeCreateDto;
import pl.edu.payroll.dto.EmployeeResponseDto;
import pl.edu.payroll.dto.EmployeeUpdateDto;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.UserRepository;
import pl.edu.payroll.service.EmployeeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    public EmployeeController(EmployeeService employeeService, UserRepository userRepository) {
        this.employeeService = employeeService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<EmployeeResponseDto>> list(
            @RequestParam(required = false) String search,
            Authentication auth) {
        Long userId = resolveUserId(auth);
        return ResponseEntity.ok(employeeService.listForEmployer(userId, search));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployeeResponseDto> create(
            @Valid @RequestBody EmployeeCreateDto dto,
            Authentication auth) {
        Long userId = resolveUserId(auth);
        try {
            return ResponseEntity.ok(employeeService.create(dto, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = resolveUserId(auth);
            String role = resolveRole(auth);
            return ResponseEntity.ok(employeeService.getById(id, userId, role));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateDto dto) {
        try {
            return ResponseEntity.ok(employeeService.update(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            employeeService.deactivate(id);
            return ResponseEntity.ok(Map.of("message", "Pracownik dezaktywowany"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<EmployeeResponseDto> getMe(Authentication auth) {
        Long userId = resolveUserId(auth);
        return ResponseEntity.ok(employeeService.getByUserId(userId));
    }

    private Long resolveUserId(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return user.getId();
    }

    private String resolveRole(Authentication auth) {
        return auth.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse("EMPLOYEE");
    }
}

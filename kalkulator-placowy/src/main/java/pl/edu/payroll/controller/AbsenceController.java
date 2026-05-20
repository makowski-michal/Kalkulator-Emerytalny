package pl.edu.payroll.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.payroll.dto.AbsenceDto;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.UserRepository;
import pl.edu.payroll.service.AbsenceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/absences")
public class AbsenceController {

    private final AbsenceService absenceService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public AbsenceController(AbsenceService absenceService,
                              UserRepository userRepository,
                              EmployeeRepository employeeRepository) {
        this.absenceService = absenceService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<AbsenceDto>> list(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication auth) {
        Long targetEmployeeId = resolveEmployeeId(employeeId, auth);
        return ResponseEntity.ok(absenceService.list(targetEmployeeId, year, month));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<AbsenceDto> create(@Valid @RequestBody AbsenceDto dto) {
        try {
            return ResponseEntity.ok(absenceService.create(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            absenceService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Nieobecność usunięta"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long resolveEmployeeId(Long requestedId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        String role = auth.getAuthorities().stream().findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", "")).orElse("EMPLOYEE");

        if ("EMPLOYER".equals(role)) {
            if (requestedId == null) throw new IllegalArgumentException("Podaj employeeId");
            return requestedId;
        }
        // EMPLOYEE – only own absences
        return employeeRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Brak profilu pracownika"))
            .getId();
    }
}

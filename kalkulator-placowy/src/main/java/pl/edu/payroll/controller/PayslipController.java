package pl.edu.payroll.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.payroll.dto.PayslipRequestDto;
import pl.edu.payroll.dto.PayslipResponseDto;
import pl.edu.payroll.entity.Payslip;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.UserRepository;
import pl.edu.payroll.service.PdfService;
import pl.edu.payroll.service.PayslipService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payslips")
public class PayslipController {

    private final PayslipService payslipService;
    private final PdfService pdfService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public PayslipController(PayslipService payslipService,
                              PdfService pdfService,
                              UserRepository userRepository,
                              EmployeeRepository employeeRepository) {
        this.payslipService = payslipService;
        this.pdfService = pdfService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<PayslipResponseDto>> list(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication auth) {
        Long targetId = resolveEmployeeId(employeeId, auth);
        return ResponseEntity.ok(payslipService.list(targetId, year, month));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> generate(
            @Valid @RequestBody PayslipRequestDto dto,
            @RequestParam(defaultValue = "false") boolean preview) {
        try {
            return ResponseEntity.ok(payslipService.generate(dto, preview));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayslipResponseDto> getById(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = resolveUserId(auth);
            String role = resolveRole(auth);
            return ResponseEntity.ok(payslipService.getById(id, userId, role));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = resolveUserId(auth);
            String role = resolveRole(auth);
            Payslip payslip = payslipService.getEntityById(id, userId, role);
            byte[] pdfBytes = pdfService.generatePayslipPdf(payslip);

            String filename = String.format("pasek_%02d_%d.pdf",
                payslip.getPeriodMonth(), payslip.getPeriodYear());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getLatest(Authentication auth) {
        Long userId = resolveUserId(auth);
        Long empId = employeeRepository.findByUserId(userId)
            .orElseThrow().getId();
        PayslipResponseDto dto = payslipService.getLatestForEmployee(empId);
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }

    private Long resolveUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow().getId();
    }

    private Long resolveEmployeeId(Long requestedId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        String role = resolveRole(auth);
        if ("EMPLOYER".equals(role)) {
            if (requestedId == null) throw new IllegalArgumentException("Podaj employeeId");
            return requestedId;
        }
        return employeeRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Brak profilu pracownika")).getId();
    }

    private String resolveRole(Authentication auth) {
        return auth.getAuthorities().stream().findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", "")).orElse("EMPLOYEE");
    }
}

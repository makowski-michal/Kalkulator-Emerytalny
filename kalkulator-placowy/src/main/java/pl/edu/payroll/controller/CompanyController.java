package pl.edu.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.edu.payroll.entity.Company;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.CompanyRepository;
import pl.edu.payroll.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/company")
@PreAuthorize("hasRole('EMPLOYER')")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyController(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> get(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return companyRepository.findByOwnerId(user.getId())
            .map(c -> ResponseEntity.ok(Map.of(
                "id", c.getId(),
                "name", c.getName(),
                "nip", c.getNip() != null ? c.getNip() : "",
                "regon", c.getRegon() != null ? c.getRegon() : ""
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping
    public ResponseEntity<?> update(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Company company = companyRepository.findByOwnerId(user.getId())
            .orElseThrow(() -> new IllegalStateException("Firma nie istnieje"));

        if (body.containsKey("name"))  company.setName(body.get("name"));
        if (body.containsKey("nip"))   company.setNip(body.get("nip"));
        if (body.containsKey("regon")) company.setRegon(body.get("regon"));
        companyRepository.save(company);

        return ResponseEntity.ok(Map.of("message", "Dane firmy zaktualizowane"));
    }
}

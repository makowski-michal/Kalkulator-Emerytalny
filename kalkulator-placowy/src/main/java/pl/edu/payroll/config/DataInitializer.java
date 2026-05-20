package pl.edu.payroll.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.payroll.entity.Company;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.CompanyRepository;
import pl.edu.payroll.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CompanyRepository companyRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail("admin@firma.pl")) return;

        User employer = new User();
        employer.setEmail("admin@firma.pl");
        employer.setPasswordHash(passwordEncoder.encode("Admin123!"));
        employer.setRole("EMPLOYER");
        userRepository.save(employer);

        Company company = new Company();
        company.setName("Moja Firma Sp. z o.o.");
        company.setNip("1234567890");
        company.setRegon("12345678901234");
        company.setOwner(employer);
        companyRepository.save(company);

        System.out.println(">>> Domyślne konto pracodawcy: admin@firma.pl / Admin123!");
    }
}

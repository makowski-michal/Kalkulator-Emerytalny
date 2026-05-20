package pl.edu.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.payroll.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByOwnerId(Long ownerId);
}

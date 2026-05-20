package pl.edu.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.payroll.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

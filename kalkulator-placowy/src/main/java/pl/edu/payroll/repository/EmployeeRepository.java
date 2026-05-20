package pl.edu.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.payroll.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByCompanyId(Long companyId);

    @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId " +
           "AND (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(e.pesel) LIKE LOWER(CONCAT('%',:search,'%')))")
    List<Employee> searchByCompany(@Param("companyId") Long companyId,
                                   @Param("search") String search);
}

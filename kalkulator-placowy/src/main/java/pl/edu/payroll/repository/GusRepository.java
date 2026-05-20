package pl.edu.payroll.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.payroll.entity.GusLifeExpectancy;

import java.util.List;
import java.util.Optional;

public interface GusRepository extends JpaRepository<GusLifeExpectancy, Long> {

    @Query("SELECT g FROM GusLifeExpectancy g WHERE g.age = :age AND g.gender = :gender " +
           "ORDER BY g.year DESC")
    List<GusLifeExpectancy> findByAgeAndGenderOrderByYearDesc(
            @Param("age") int age,
            @Param("gender") String gender,
            org.springframework.data.domain.Pageable pageable);

    default Optional<GusLifeExpectancy> findLatestByAgeAndGender(int age, String gender) {
        List<GusLifeExpectancy> results = findByAgeAndGenderOrderByYearDesc(
                age, gender, PageRequest.of(0, 1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}

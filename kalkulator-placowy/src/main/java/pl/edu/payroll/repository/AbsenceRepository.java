package pl.edu.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.payroll.entity.Absence;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    List<Absence> findByEmployeeId(Long employeeId);

    @Query("SELECT a FROM Absence a WHERE a.employee.id = :employeeId " +
           "AND (:from IS NULL OR a.dateTo >= :from) " +
           "AND (:to IS NULL OR a.dateFrom <= :to)")
    List<Absence> findByEmployeeIdInRange(@Param("employeeId") Long employeeId,
                                          @Param("from") LocalDate from,
                                          @Param("to") LocalDate to);

    default List<Absence> findByEmployeeIdAndPeriod(Long employeeId, Integer year, Integer month) {
        if (year == null) return findByEmployeeId(employeeId);
        LocalDate from = (month != null)
            ? LocalDate.of(year, month, 1)
            : LocalDate.of(year, 1, 1);
        LocalDate to = (month != null)
            ? from.withDayOfMonth(from.lengthOfMonth())
            : LocalDate.of(year, 12, 31);
        return findByEmployeeIdInRange(employeeId, from, to);
    }

    @Query(value = "SELECT COALESCE(SUM(a.days_count), 0) FROM absences a " +
                   "WHERE a.employee_id = :employeeId AND a.type = 'SICK_LEAVE' " +
                   "AND EXTRACT(YEAR FROM a.date_from) = :year " +
                   "AND EXTRACT(MONTH FROM a.date_from) = :month",
           nativeQuery = true)
    int sumSickLeaveDays(@Param("employeeId") Long employeeId,
                         @Param("year") int year,
                         @Param("month") int month);

    @Query(value = "SELECT COALESCE(SUM(a.days_count), 0) FROM absences a " +
                   "WHERE a.employee_id = :employeeId AND a.type = 'UNPAID_LEAVE' " +
                   "AND EXTRACT(YEAR FROM a.date_from) = :year " +
                   "AND EXTRACT(MONTH FROM a.date_from) = :month",
           nativeQuery = true)
    int sumUnpaidLeaveDays(@Param("employeeId") Long employeeId,
                           @Param("year") int year,
                           @Param("month") int month);
}

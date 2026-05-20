package pl.edu.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.payroll.entity.Payslip;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    List<Payslip> findByEmployeeIdOrderByPeriodYearDescPeriodMonthDesc(Long employeeId);

    @Query("SELECT p FROM Payslip p WHERE p.employee.id = :employeeId " +
           "AND (:year IS NULL OR p.periodYear = :year) " +
           "AND (:month IS NULL OR p.periodMonth = :month) " +
           "ORDER BY p.periodYear DESC, p.periodMonth DESC")
    List<Payslip> findByEmployeeAndPeriod(@Param("employeeId") Long employeeId,
                                          @Param("year") Integer year,
                                          @Param("month") Integer month);

    Optional<Payslip> findByEmployeeIdAndPeriodYearAndPeriodMonth(Long employeeId, int year, int month);

    @Query("SELECT COALESCE(SUM(p.pensionContribEmployee), 0) FROM Payslip p WHERE p.employee.id = :employeeId")
    BigDecimal sumPensionContribByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT p.periodYear, p.periodMonth, SUM(p.pensionContribEmployee) " +
           "FROM Payslip p WHERE p.employee.id = :employeeId " +
           "GROUP BY p.periodYear, p.periodMonth " +
           "ORDER BY p.periodYear ASC, p.periodMonth ASC")
    List<Object[]> monthlyPensionContribs(@Param("employeeId") Long employeeId);

    Optional<Payslip> findTopByEmployeeIdOrderByPeriodYearDescPeriodMonthDesc(Long employeeId);
}

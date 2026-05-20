package pl.edu.payroll.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.payroll.dto.AbsenceDto;
import pl.edu.payroll.entity.Absence;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.repository.AbsenceRepository;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.PayslipRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final EmployeeRepository employeeRepository;
    private final PayslipRepository payslipRepository;

    public AbsenceService(AbsenceRepository absenceRepository,
                          EmployeeRepository employeeRepository,
                          PayslipRepository payslipRepository) {
        this.absenceRepository = absenceRepository;
        this.employeeRepository = employeeRepository;
        this.payslipRepository = payslipRepository;
    }

    @Transactional(readOnly = true)
    public List<AbsenceDto> list(Long employeeId, Integer year, Integer month) {
        return absenceRepository.findByEmployeeIdAndPeriod(employeeId, year, month)
            .stream().map(AbsenceDto::from).toList();
    }

    public AbsenceDto create(AbsenceDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));

        Absence absence = new Absence();
        absence.setEmployee(employee);
        absence.setType(dto.getType());
        absence.setDateFrom(dto.getDateFrom());
        absence.setDateTo(dto.getDateTo());
        absence.setDaysCount(countWorkingDays(dto.getDateFrom(), dto.getDateTo()));
        absence.setNote(dto.getNote());

        return AbsenceDto.from(absenceRepository.save(absence));
    }

    public void delete(Long absenceId) {
        Absence absence = absenceRepository.findById(absenceId)
            .orElseThrow(() -> new IllegalArgumentException("Nieobecność nie istnieje"));

        LocalDate from = absence.getDateFrom();
        boolean payslipExists = payslipRepository
            .findByEmployeeIdAndPeriodYearAndPeriodMonth(
                absence.getEmployee().getId(), from.getYear(), from.getMonthValue())
            .isPresent();

        if (payslipExists) {
            throw new IllegalStateException("Nie można usunąć – pasek za ten okres już istnieje");
        }
        absenceRepository.delete(absence);
    }

    private static int countWorkingDays(LocalDate from, LocalDate to) {
        int count = 0;
        LocalDate d = from;
        while (!d.isAfter(to)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) count++;
            d = d.plusDays(1);
        }
        return count;
    }
}

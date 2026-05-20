package pl.edu.payroll.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.payroll.dto.PayslipRequestDto;
import pl.edu.payroll.dto.PayslipResponseDto;
import pl.edu.payroll.entity.Employee;
import pl.edu.payroll.entity.Payslip;
import pl.edu.payroll.repository.EmployeeRepository;
import pl.edu.payroll.repository.PayslipRepository;

import java.util.List;

@Service
@Transactional
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollService payrollService;

    public PayslipService(PayslipRepository payslipRepository,
                          EmployeeRepository employeeRepository,
                          PayrollService payrollService) {
        this.payslipRepository = payslipRepository;
        this.employeeRepository = employeeRepository;
        this.payrollService = payrollService;
    }

    public PayslipResponseDto generate(PayslipRequestDto req, boolean preview) {
        Employee employee = employeeRepository.findById(req.getEmployeeId())
            .orElseThrow(() -> new IllegalArgumentException("Pracownik nie istnieje"));

        boolean exists = payslipRepository
            .findByEmployeeIdAndPeriodYearAndPeriodMonth(
                employee.getId(), req.getPeriodYear(), req.getPeriodMonth())
            .isPresent();
        if (exists && !preview) {
            throw new IllegalStateException("Pasek za ten okres już istnieje");
        }

        Payslip payslip = payrollService.calculate(employee, req);

        if (!preview) {
            payslipRepository.save(payslip);
        }
        return PayslipResponseDto.from(payslip);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> list(Long employeeId, Integer year, Integer month) {
        return payslipRepository.findByEmployeeAndPeriod(employeeId, year, month)
            .stream().map(PayslipResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public PayslipResponseDto getById(Long id, Long requestingUserId, String requestingRole) {
        Payslip payslip = payslipRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pasek nie istnieje"));

        if ("EMPLOYER".equals(requestingRole)) {
            return PayslipResponseDto.from(payslip);
        }
        if (!payslip.getEmployee().getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Brak dostępu");
        }
        return PayslipResponseDto.from(payslip);
    }

    @Transactional(readOnly = true)
    public Payslip getEntityById(Long id, Long requestingUserId, String requestingRole) {
        Payslip payslip = payslipRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pasek nie istnieje"));

        if ("EMPLOYER".equals(requestingRole)) return payslip;
        if (!payslip.getEmployee().getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Brak dostępu");
        }
        return payslip;
    }

    @Transactional(readOnly = true)
    public PayslipResponseDto getLatestForEmployee(Long employeeId) {
        return payslipRepository
            .findTopByEmployeeIdOrderByPeriodYearDescPeriodMonthDesc(employeeId)
            .map(PayslipResponseDto::from)
            .orElse(null);
    }
}

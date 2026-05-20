package pl.edu.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.edu.payroll.entity.Absence;

import java.time.LocalDate;

public class AbsenceDto {

    private Long id;
    private Long employeeId;

    @NotBlank
    private String type;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    private LocalDate dateTo;

    private int daysCount;
    private String note;

    public static AbsenceDto from(Absence a) {
        AbsenceDto dto = new AbsenceDto();
        dto.id = a.getId();
        dto.employeeId = a.getEmployee().getId();
        dto.type = a.getType();
        dto.dateFrom = a.getDateFrom();
        dto.dateTo = a.getDateTo();
        dto.daysCount = a.getDaysCount();
        dto.note = a.getNote();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }

    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }

    public int getDaysCount() { return daysCount; }
    public void setDaysCount(int daysCount) { this.daysCount = daysCount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

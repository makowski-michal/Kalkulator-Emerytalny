package pl.edu.payroll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gus_life_expectancy",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year","age","gender"}))
public class GusLifeExpectancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(name = "months_remaining", nullable = false, precision = 6, scale = 2)
    private BigDecimal monthsRemaining;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public BigDecimal getMonthsRemaining() { return monthsRemaining; }
    public void setMonthsRemaining(BigDecimal monthsRemaining) { this.monthsRemaining = monthsRemaining; }
}

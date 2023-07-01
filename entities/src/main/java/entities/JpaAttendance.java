package entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class JpaAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attendance_id")
    private Long attendanceId;
    @Column(name = "attendance_date")
    private LocalDate date;
    @Column(name = "time_in")
    private LocalDateTime timeIn;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private JpaEmployee employee;

    public JpaAttendance(){}
    public JpaAttendance(JpaEmployee employee, LocalDate date, LocalDateTime timeIn){
        this.employee = employee;
        this.date = date;
        this.timeIn = timeIn;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalDateTime timeIn) {
        this.timeIn = timeIn;
    }

    public JpaEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(JpaEmployee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + attendanceId +
                ", date=" + date +
                ", timeIn=" + timeIn +
                ", employee=" + employee +
                '}';
    }
}


package org.encentral.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private Long attendanceId;
    private LocalDate date;
    private LocalDateTime timeIn;
    private Employee employee;

    public Attendance(){}

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

    public Employee getEmployeeDTO() {
        return employee;
    }

    public void setEmployeeDTO(Employee employeeDTOId) {
        this.employee = employeeDTOId;
    }

    @Override
    public String toString() {
        return "AttendanceDTO{" +
                "attendanceId=" + attendanceId +
                ", date=" + date +
                ", timeIn=" + timeIn +
                '}';
    }
}

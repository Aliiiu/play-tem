package org.encentral.model;

import entities.JpaAttendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceMapper {

    public static Attendance toAttendance(JpaAttendance jpaAttendance){
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(jpaAttendance.getAttendanceId());
        attendance.setDate(jpaAttendance.getDate());
        attendance.setTimeIn(jpaAttendance.getTimeIn());
        attendance.setEmployeeDTO(EmployeeMapper.toEmployee(jpaAttendance.getEmployee()));
        return attendance;
    }

    public static List<Attendance> toAttendanceList(List<JpaAttendance> jpaAttendanceList){
        List<Attendance> attendanceDTOList = new ArrayList<>();
        for (JpaAttendance attendance : jpaAttendanceList){
            attendanceDTOList.add(toAttendance(attendance));
        }
        return attendanceDTOList;
    }

    public static List<JpaAttendance> fromAttendanceList(List<Attendance> attendanceList){
        List<JpaAttendance> jpaAttendanceList = new ArrayList<>();
        for (Attendance attendance : attendanceList){
            jpaAttendanceList.add(fromAttendance(attendance));
        }
        return jpaAttendanceList;
    }

    public static JpaAttendance fromAttendance(Attendance attendance){
        JpaAttendance jpaAttendance = new JpaAttendance();
        jpaAttendance.setAttendanceId(attendance.getAttendanceId());
        jpaAttendance.setDate(attendance.getDate());
        jpaAttendance.setTimeIn(attendance.getTimeIn());
        jpaAttendance.setEmployee(EmployeeMapper.fromEmployee(attendance.getEmployeeDTO()));
        return jpaAttendance;
    }

}

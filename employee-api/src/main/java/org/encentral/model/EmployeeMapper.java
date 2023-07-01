package org.encentral.model;

import entities.JpaEmployee;

public class EmployeeMapper {
    public static Employee toEmployee(JpaEmployee jpaEmployee){
        Employee employee = new Employee();
        employee.setEmployeeId(jpaEmployee.getEmployeeId());
        employee.setEmail(jpaEmployee.getEmail());
        employee.setPassword(jpaEmployee.getPassword());
        employee.setToken(jpaEmployee.getToken());
        employee.setRole(jpaEmployee.getRole());
        employee.setRole(jpaEmployee.getRole());
        employee.setCreatedAt(jpaEmployee.getCreatedAt());
        employee.setFirstName(jpaEmployee.getFirstName());
        employee.setLastName(jpaEmployee.getLastName());
        return employee;
    }

    public static JpaEmployee fromEmployee(Employee employee){
        JpaEmployee jpaEmployee = new JpaEmployee();
        jpaEmployee.setEmployeeId(employee.getEmployeeId());
        jpaEmployee.setEmail(employee.getEmail());
        jpaEmployee.setPassword(employee.getPassword());
        jpaEmployee.setToken(employee.getToken());
        jpaEmployee.setRole(employee.getRole());
        jpaEmployee.setFirstName(employee.getFirstName());
        jpaEmployee.setLastName(employee.getLastName());
        jpaEmployee.setCreatedAt(employee.getCreatedAt());
        return jpaEmployee;
    }
}

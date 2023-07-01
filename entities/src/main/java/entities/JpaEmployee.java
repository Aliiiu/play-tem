package entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Table(name = "employee")
@Entity
public class JpaEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "employee_id")
    private Long employeeId;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Column(name = "token", unique = true)
    private String token;
    @Column(name = "employee_role", nullable = false)
    private Role role;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employee")
    private List<JpaAttendance> attendances;

    public JpaEmployee(){}

    public JpaEmployee(String email, String password, Role role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeID) {
        this.employeeId = employeeID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<JpaAttendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<JpaAttendance> attendances) {
        this.attendances = attendances;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaEmployee)) return false;
        JpaEmployee employee = (JpaEmployee) o;
        return Objects.equals(employeeId, employee.employeeId) && Objects.equals(email, employee.email) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(password, employee.password) && Objects.equals(token, employee.token) && role == employee.role && Objects.equals(createdAt, employee.createdAt) && Objects.equals(attendances, employee.attendances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, email, firstName, lastName, password, token, role, createdAt, attendances);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID=" + employeeId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}


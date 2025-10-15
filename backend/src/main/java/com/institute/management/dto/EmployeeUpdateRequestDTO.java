package com.institute.management.dto;

import com.institute.management.entity.Employee;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO for updating an existing employee
 */
public class EmployeeUpdateRequestDTO {
    
    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    private String employeeCode;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;
    
    @Size(max = 50, message = "Department must not exceed 50 characters")
    private String department;
    
    @NotNull(message = "Role is required")
    private Employee.EmployeeRole role;
    
    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;
    
    @NotNull(message = "Status is required")
    private Employee.EmployeeStatus status;
    
    // Constructors
    public EmployeeUpdateRequestDTO() {}
    
    // Getters and Setters
    public String getEmployeeCode() {
        return employeeCode;
    }
    
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Employee.EmployeeRole getRole() {
        return role;
    }
    
    public void setRole(Employee.EmployeeRole role) {
        this.role = role;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public Employee.EmployeeStatus getStatus() {
        return status;
    }
    
    public void setStatus(Employee.EmployeeStatus status) {
        this.status = status;
    }
}
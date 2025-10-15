package com.institute.management.dto;

import com.institute.management.entity.Employee;

import java.util.UUID;

/**
 * Basic DTO for employee information used in relationships
 */
public class EmployeeBasicDTO {
    
    private UUID id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private Employee.EmployeeRole role;
    private String department;
    
    // Constructors
    public EmployeeBasicDTO() {}
    
    public EmployeeBasicDTO(UUID id, String employeeCode, String firstName, String lastName, 
                           String email, Employee.EmployeeRole role, String department) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.role = role;
        this.department = department;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Employee.EmployeeRole getRole() {
        return role;
    }
    
    public void setRole(Employee.EmployeeRole role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}
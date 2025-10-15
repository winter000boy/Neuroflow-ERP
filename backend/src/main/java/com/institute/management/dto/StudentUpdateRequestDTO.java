package com.institute.management.dto;

import com.institute.management.entity.Student;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing student
 */
public class StudentUpdateRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private String address;
    
    private UUID batchId;
    
    @NotNull(message = "Status is required")
    private Student.StudentStatus status;
    
    private LocalDate graduationDate;
    
    @Size(max = 5, message = "Final grade must not exceed 5 characters")
    private String finalGrade;
    
    // Constructors
    public StudentUpdateRequestDTO() {}
    
    // Getters and Setters
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
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public UUID getBatchId() {
        return batchId;
    }
    
    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }
    
    public Student.StudentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Student.StudentStatus status) {
        this.status = status;
    }
    
    public LocalDate getGraduationDate() {
        return graduationDate;
    }
    
    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
    }
    
    public String getFinalGrade() {
        return finalGrade;
    }
    
    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}
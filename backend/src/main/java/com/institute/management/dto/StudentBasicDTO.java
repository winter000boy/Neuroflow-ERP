package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.institute.management.entity.Student;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Basic DTO for student information used in relationships
 */
public class StudentBasicDTO {
    
    private UUID id;
    private String enrollmentNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private Student.StudentStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;
    
    // Constructors
    public StudentBasicDTO() {}
    
    public StudentBasicDTO(UUID id, String enrollmentNumber, String firstName, String lastName, 
                          String email, Student.StudentStatus status, LocalDate enrollmentDate) {
        this.id = id;
        this.enrollmentNumber = enrollmentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }
    
    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
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
    
    public Student.StudentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Student.StudentStatus status) {
        this.status = status;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.institute.management.entity.Lead;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Basic DTO for lead information used in relationships
 */
public class LeadBasicDTO {
    
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String courseInterest;
    private Lead.LeadStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    // Constructors
    public LeadBasicDTO() {}
    
    public LeadBasicDTO(UUID id, String firstName, String lastName, String email, 
                       String phone, String courseInterest, Lead.LeadStatus status, LocalDateTime createdDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.phone = phone;
        this.courseInterest = courseInterest;
        this.status = status;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCourseInterest() {
        return courseInterest;
    }
    
    public void setCourseInterest(String courseInterest) {
        this.courseInterest = courseInterest;
    }
    
    public Lead.LeadStatus getStatus() {
        return status;
    }
    
    public void setStatus(Lead.LeadStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
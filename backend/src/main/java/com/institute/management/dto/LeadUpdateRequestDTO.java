package com.institute.management.dto;

import com.institute.management.entity.Lead;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for updating an existing lead
 */
public class LeadUpdateRequestDTO {
    
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
    
    @Size(max = 100, message = "Course interest must not exceed 100 characters")
    private String courseInterest;
    
    @Size(max = 50, message = "Source must not exceed 50 characters")
    private String source;
    
    @NotNull(message = "Status is required")
    private Lead.LeadStatus status;
    
    private UUID assignedCounsellorId;
    
    private String notes;
    
    private LocalDateTime nextFollowUpDate;
    
    // Constructors
    public LeadUpdateRequestDTO() {}
    
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
    
    public String getCourseInterest() {
        return courseInterest;
    }
    
    public void setCourseInterest(String courseInterest) {
        this.courseInterest = courseInterest;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Lead.LeadStatus getStatus() {
        return status;
    }
    
    public void setStatus(Lead.LeadStatus status) {
        this.status = status;
    }
    
    public UUID getAssignedCounsellorId() {
        return assignedCounsellorId;
    }
    
    public void setAssignedCounsellorId(UUID assignedCounsellorId) {
        this.assignedCounsellorId = assignedCounsellorId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getNextFollowUpDate() {
        return nextFollowUpDate;
    }
    
    public void setNextFollowUpDate(LocalDateTime nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }
}
package com.institute.management.dto;

import com.institute.management.entity.Placement;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new placement
 */
public class PlacementCreateRequestDTO {
    
    @NotNull(message = "Student is required")
    private UUID studentId;
    
    @NotNull(message = "Company is required")
    private UUID companyId;
    
    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;
    
    @NotNull(message = "Placement date is required")
    private LocalDate placementDate;
    
    private Placement.JobType jobType;
    
    @Size(max = 100, message = "Work location must not exceed 100 characters")
    private String workLocation;
    
    private Placement.EmploymentType employmentType;
    
    @Min(value = 0, message = "Probation period cannot be negative")
    @Max(value = 24, message = "Probation period cannot exceed 24 months")
    private Integer probationPeriodMonths;
    
    private LocalDate joiningDate;
    
    private String notes;
    
    // Constructors
    public PlacementCreateRequestDTO() {}
    
    // Getters and Setters
    public UUID getStudentId() {
        return studentId;
    }
    
    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }
    
    public UUID getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public LocalDate getPlacementDate() {
        return placementDate;
    }
    
    public void setPlacementDate(LocalDate placementDate) {
        this.placementDate = placementDate;
    }
    
    public Placement.JobType getJobType() {
        return jobType;
    }
    
    public void setJobType(Placement.JobType jobType) {
        this.jobType = jobType;
    }
    
    public String getWorkLocation() {
        return workLocation;
    }
    
    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }
    
    public Placement.EmploymentType getEmploymentType() {
        return employmentType;
    }
    
    public void setEmploymentType(Placement.EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
    
    public Integer getProbationPeriodMonths() {
        return probationPeriodMonths;
    }
    
    public void setProbationPeriodMonths(Integer probationPeriodMonths) {
        this.probationPeriodMonths = probationPeriodMonths;
    }
    
    public LocalDate getJoiningDate() {
        return joiningDate;
    }
    
    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
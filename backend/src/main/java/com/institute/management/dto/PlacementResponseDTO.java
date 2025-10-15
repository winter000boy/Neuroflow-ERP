package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.institute.management.entity.Placement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for placement response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacementResponseDTO {
    
    private UUID id;
    private StudentBasicDTO student;
    private CompanyBasicDTO company;
    private String position;
    private BigDecimal salary;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate placementDate;
    
    private Placement.PlacementStatus status;
    private Placement.JobType jobType;
    private String workLocation;
    private Placement.EmploymentType employmentType;
    private Integer probationPeriodMonths;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String notes;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    // Computed fields
    private Boolean isActive;
    private Boolean isInProbation;
    private Long tenureInMonths;
    
    // Constructors
    public PlacementResponseDTO() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public StudentBasicDTO getStudent() {
        return student;
    }
    
    public void setStudent(StudentBasicDTO student) {
        this.student = student;
    }
    
    public CompanyBasicDTO getCompany() {
        return company;
    }
    
    public void setCompany(CompanyBasicDTO company) {
        this.company = company;
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
    
    public Placement.PlacementStatus getStatus() {
        return status;
    }
    
    public void setStatus(Placement.PlacementStatus status) {
        this.status = status;
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
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsInProbation() {
        return isInProbation;
    }
    
    public void setIsInProbation(Boolean isInProbation) {
        this.isInProbation = isInProbation;
    }
    
    public Long getTenureInMonths() {
        return tenureInMonths;
    }
    
    public void setTenureInMonths(Long tenureInMonths) {
        this.tenureInMonths = tenureInMonths;
    }
}
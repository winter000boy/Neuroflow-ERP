package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.institute.management.entity.Placement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Basic DTO for placement information used in relationships
 */
public class PlacementBasicDTO {
    
    private UUID id;
    private CompanyBasicDTO company;
    private String position;
    private BigDecimal salary;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate placementDate;
    
    private Placement.PlacementStatus status;
    private Placement.JobType jobType;
    private Placement.EmploymentType employmentType;
    
    // Constructors
    public PlacementBasicDTO() {}
    
    public PlacementBasicDTO(UUID id, String position, BigDecimal salary, LocalDate placementDate, 
                            Placement.PlacementStatus status, Placement.JobType jobType, 
                            Placement.EmploymentType employmentType) {
        this.id = id;
        this.position = position;
        this.salary = salary;
        this.placementDate = placementDate;
        this.status = status;
        this.jobType = jobType;
        this.employmentType = employmentType;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public Placement.EmploymentType getEmploymentType() {
        return employmentType;
    }
    
    public void setEmploymentType(Placement.EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}
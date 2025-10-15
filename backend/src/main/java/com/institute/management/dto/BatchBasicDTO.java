package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.institute.management.entity.Batch;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Basic DTO for batch information used in relationships
 */
public class BatchBasicDTO {
    
    private UUID id;
    private String name;
    private CourseBasicDTO course;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Integer capacity;
    private Integer currentEnrollment;
    private Batch.BatchStatus status;
    private EmployeeBasicDTO instructor;
    
    // Constructors
    public BatchBasicDTO() {}
    
    public BatchBasicDTO(UUID id, String name, LocalDate startDate, LocalDate endDate, 
                        Integer capacity, Integer currentEnrollment, Batch.BatchStatus status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.currentEnrollment = currentEnrollment;
        this.status = status;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CourseBasicDTO getCourse() {
        return course;
    }
    
    public void setCourse(CourseBasicDTO course) {
        this.course = course;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(Integer currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public Batch.BatchStatus getStatus() {
        return status;
    }
    
    public void setStatus(Batch.BatchStatus status) {
        this.status = status;
    }
    
    public EmployeeBasicDTO getInstructor() {
        return instructor;
    }
    
    public void setInstructor(EmployeeBasicDTO instructor) {
        this.instructor = instructor;
    }
}
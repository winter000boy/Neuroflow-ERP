package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.institute.management.entity.Batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for batch response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchResponseDTO {
    
    private UUID id;
    private String name;
    private CourseBasicDTO course;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Integer capacity;
    private Integer currentEnrollment;
    private Integer availableSlots;
    private Double utilizationPercentage;
    private Batch.BatchStatus status;
    private EmployeeBasicDTO instructor;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private List<StudentBasicDTO> students;
    
    // Constructors
    public BatchResponseDTO() {}
    
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
    
    public Integer getAvailableSlots() {
        return availableSlots;
    }
    
    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
    
    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }
    
    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
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
    
    public List<StudentBasicDTO> getStudents() {
        return students;
    }
    
    public void setStudents(List<StudentBasicDTO> students) {
        this.students = students;
    }
}
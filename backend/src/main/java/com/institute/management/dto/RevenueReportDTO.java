package com.institute.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RevenueReportDTO {
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private BigDecimal totalRevenue;
    
    private BigDecimal projectedRevenue;
    
    private Map<String, BigDecimal> revenueByMonth;
    
    private Map<String, BigDecimal> revenueByCourse;
    
    private Map<String, BigDecimal> revenueByBatch;
    
    private BigDecimal averageRevenuePerStudent;
    
    private Integer totalEnrollments;
    
    private BigDecimal growthRate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate generatedAt;
    
    // Constructors
    public RevenueReportDTO() {}
    
    public RevenueReportDTO(LocalDate startDate, LocalDate endDate, BigDecimal totalRevenue, 
                           BigDecimal projectedRevenue, Map<String, BigDecimal> revenueByMonth,
                           Map<String, BigDecimal> revenueByCourse, Map<String, BigDecimal> revenueByBatch,
                           BigDecimal averageRevenuePerStudent, Integer totalEnrollments, 
                           BigDecimal growthRate, LocalDate generatedAt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue;
        this.projectedRevenue = projectedRevenue;
        this.revenueByMonth = revenueByMonth;
        this.revenueByCourse = revenueByCourse;
        this.revenueByBatch = revenueByBatch;
        this.averageRevenuePerStudent = averageRevenuePerStudent;
        this.totalEnrollments = totalEnrollments;
        this.growthRate = growthRate;
        this.generatedAt = generatedAt;
    }
    
    // Getters and Setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public BigDecimal getProjectedRevenue() { return projectedRevenue; }
    public void setProjectedRevenue(BigDecimal projectedRevenue) { this.projectedRevenue = projectedRevenue; }
    
    public Map<String, BigDecimal> getRevenueByMonth() { return revenueByMonth; }
    public void setRevenueByMonth(Map<String, BigDecimal> revenueByMonth) { this.revenueByMonth = revenueByMonth; }
    
    public Map<String, BigDecimal> getRevenueByCourse() { return revenueByCourse; }
    public void setRevenueByCourse(Map<String, BigDecimal> revenueByCourse) { this.revenueByCourse = revenueByCourse; }
    
    public Map<String, BigDecimal> getRevenueByBatch() { return revenueByBatch; }
    public void setRevenueByBatch(Map<String, BigDecimal> revenueByBatch) { this.revenueByBatch = revenueByBatch; }
    
    public BigDecimal getAverageRevenuePerStudent() { return averageRevenuePerStudent; }
    public void setAverageRevenuePerStudent(BigDecimal averageRevenuePerStudent) { this.averageRevenuePerStudent = averageRevenuePerStudent; }
    
    public Integer getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(Integer totalEnrollments) { this.totalEnrollments = totalEnrollments; }
    
    public BigDecimal getGrowthRate() { return growthRate; }
    public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
    
    public LocalDate getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; }
}
package com.institute.management.dto;

/**
 * DTO for company statistics
 */
public class CompanyStatisticsDTO {
    
    private long totalCompanies;
    private long activeCompanies;
    private long inactiveCompanies;
    private long blacklistedCompanies;
    private long companiesWithPlacements;
    private long companiesWithoutPlacements;
    private long distinctIndustries;
    private long recentPartners;
    private long longTermPartners;
    
    // Constructors
    public CompanyStatisticsDTO() {}
    
    // Getters and Setters
    public long getTotalCompanies() {
        return totalCompanies;
    }
    
    public void setTotalCompanies(long totalCompanies) {
        this.totalCompanies = totalCompanies;
    }
    
    public long getActiveCompanies() {
        return activeCompanies;
    }
    
    public void setActiveCompanies(long activeCompanies) {
        this.activeCompanies = activeCompanies;
    }
    
    public long getInactiveCompanies() {
        return inactiveCompanies;
    }
    
    public void setInactiveCompanies(long inactiveCompanies) {
        this.inactiveCompanies = inactiveCompanies;
    }
    
    public long getBlacklistedCompanies() {
        return blacklistedCompanies;
    }
    
    public void setBlacklistedCompanies(long blacklistedCompanies) {
        this.blacklistedCompanies = blacklistedCompanies;
    }
    
    public long getCompaniesWithPlacements() {
        return companiesWithPlacements;
    }
    
    public void setCompaniesWithPlacements(long companiesWithPlacements) {
        this.companiesWithPlacements = companiesWithPlacements;
    }
    
    public long getCompaniesWithoutPlacements() {
        return companiesWithoutPlacements;
    }
    
    public void setCompaniesWithoutPlacements(long companiesWithoutPlacements) {
        this.companiesWithoutPlacements = companiesWithoutPlacements;
    }
    
    public long getDistinctIndustries() {
        return distinctIndustries;
    }
    
    public void setDistinctIndustries(long distinctIndustries) {
        this.distinctIndustries = distinctIndustries;
    }
    
    public long getRecentPartners() {
        return recentPartners;
    }
    
    public void setRecentPartners(long recentPartners) {
        this.recentPartners = recentPartners;
    }
    
    public long getLongTermPartners() {
        return longTermPartners;
    }
    
    public void setLongTermPartners(long longTermPartners) {
        this.longTermPartners = longTermPartners;
    }
}
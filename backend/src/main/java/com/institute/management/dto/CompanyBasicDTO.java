package com.institute.management.dto;

import com.institute.management.entity.Company;

import java.util.UUID;

/**
 * Basic DTO for company information used in relationships
 */
public class CompanyBasicDTO {
    
    private UUID id;
    private String name;
    private String industry;
    private String contactPerson;
    private String email;
    private Company.CompanyStatus status;
    
    // Constructors
    public CompanyBasicDTO() {}
    
    public CompanyBasicDTO(UUID id, String name, String industry, String contactPerson, 
                          String email, Company.CompanyStatus status) {
        this.id = id;
        this.name = name;
        this.industry = industry;
        this.contactPerson = contactPerson;
        this.email = email;
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
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Company.CompanyStatus getStatus() {
        return status;
    }
    
    public void setStatus(Company.CompanyStatus status) {
        this.status = status;
    }
}
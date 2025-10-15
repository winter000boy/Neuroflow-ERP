package com.institute.management.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO for creating a new company
 */
public class CompanyCreateRequestDTO {
    
    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String name;
    
    @Size(max = 50, message = "Industry must not exceed 50 characters")
    private String industry;
    
    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPerson;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phone;
    
    private String address;
    
    private LocalDate partnershipDate;
    
    // Constructors
    public CompanyCreateRequestDTO() {}
    
    // Getters and Setters
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getPartnershipDate() {
        return partnershipDate;
    }
    
    public void setPartnershipDate(LocalDate partnershipDate) {
        this.partnershipDate = partnershipDate;
    }
}
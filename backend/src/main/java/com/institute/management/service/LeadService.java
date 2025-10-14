package com.institute.management.service;

import com.institute.management.entity.Employee;
import com.institute.management.entity.Lead;
import com.institute.management.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    /**
     * Create a new lead - Only ADMIN and COUNSELLOR can create leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Lead createLead(Lead lead) {
        return leadRepository.save(lead);
    }
    
    /**
     * Update an existing lead - Only ADMIN and COUNSELLOR can update leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Lead updateLead(UUID id, Lead leadDetails) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
        
        lead.setFirstName(leadDetails.getFirstName());
        lead.setLastName(leadDetails.getLastName());
        lead.setEmail(leadDetails.getEmail());
        lead.setPhone(leadDetails.getPhone());
        lead.setCourseInterest(leadDetails.getCourseInterest());
        lead.setSource(leadDetails.getSource());
        lead.setStatus(leadDetails.getStatus());
        
        return leadRepository.save(lead);
    }
    
    /**
     * Get lead by ID - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Optional<Lead> getLeadById(UUID id) {
        return leadRepository.findById(id);
    }
    
    /**
     * Get all leads with pagination - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Page<Lead> getAllLeads(Pageable pageable) {
        return leadRepository.findAll(pageable);
    }
    
    /**
     * Get leads by status - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public List<Lead> getLeadsByStatus(Lead.LeadStatus status) {
        return leadRepository.findByStatus(status);
    }
    
    /**
     * Convert lead to student - Only ADMIN and COUNSELLOR can convert leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Lead convertLead(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadId));
        
        lead.setStatus(Lead.LeadStatus.CONVERTED);
        lead.setConvertedDate(java.time.LocalDateTime.now());
        
        return leadRepository.save(lead);
    }
    
    /**
     * Delete lead - Only ADMIN and COUNSELLOR can delete leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public void deleteLead(UUID id) {
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }
    
    /**
     * Get leads assigned to a counsellor - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public List<Lead> getLeadsByCounsellor(Employee counsellor) {
        return leadRepository.findByAssignedCounsellor(counsellor);
    }
}
package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.*;
import com.institute.management.exception.*;
import com.institute.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private BatchRepository batchRepository;
    
    /**
     * Create a new lead - Only ADMIN and COUNSELLOR can create leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadResponseDTO createLead(LeadCreateRequestDTO createRequest) {
        // Check for duplicate email or phone
        if (createRequest.getEmail() != null && leadRepository.existsByEmail(createRequest.getEmail())) {
            throw new DuplicateResourceException("Lead with email " + createRequest.getEmail() + " already exists");
        }
        
        if (leadRepository.existsByPhone(createRequest.getPhone())) {
            throw new DuplicateResourceException("Lead with phone " + createRequest.getPhone() + " already exists");
        }
        
        Lead lead = new Lead();
        lead.setFirstName(createRequest.getFirstName());
        lead.setLastName(createRequest.getLastName());
        lead.setEmail(createRequest.getEmail());
        lead.setPhone(createRequest.getPhone());
        lead.setCourseInterest(createRequest.getCourseInterest());
        lead.setSource(createRequest.getSource());
        lead.setNotes(createRequest.getNotes());
        lead.setNextFollowUpDate(createRequest.getNextFollowUpDate());
        
        // Assign counsellor if provided
        if (createRequest.getAssignedCounsellorId() != null) {
            Employee counsellor = employeeRepository.findById(createRequest.getAssignedCounsellorId())
                .orElseThrow(() -> new ResourceNotFoundException("Counsellor not found with id: " + createRequest.getAssignedCounsellorId()));
            
            if (!counsellor.getRole().equals(Employee.EmployeeRole.COUNSELLOR) && !counsellor.getRole().equals(Employee.EmployeeRole.ADMIN)) {
                throw new ValidationException("Assigned employee must be a COUNSELLOR or ADMIN");
            }
            
            lead.setAssignedCounsellor(counsellor);
        }
        
        Lead savedLead = leadRepository.save(lead);
        return convertToResponseDTO(savedLead);
    }
    
    /**
     * Update an existing lead - Only ADMIN and COUNSELLOR can update leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadResponseDTO updateLead(UUID id, LeadUpdateRequestDTO updateRequest) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        // Check for duplicate email or phone (excluding current lead)
        if (updateRequest.getEmail() != null && 
            !updateRequest.getEmail().equals(lead.getEmail()) && 
            leadRepository.existsByEmail(updateRequest.getEmail())) {
            throw new DuplicateResourceException("Lead with email " + updateRequest.getEmail() + " already exists");
        }
        
        if (!updateRequest.getPhone().equals(lead.getPhone()) && 
            leadRepository.existsByPhone(updateRequest.getPhone())) {
            throw new DuplicateResourceException("Lead with phone " + updateRequest.getPhone() + " already exists");
        }
        
        // Validate status transition
        if (lead.getStatus() == Lead.LeadStatus.CONVERTED && updateRequest.getStatus() != Lead.LeadStatus.CONVERTED) {
            throw new ValidationException("Cannot change status of a converted lead");
        }
        
        lead.setFirstName(updateRequest.getFirstName());
        lead.setLastName(updateRequest.getLastName());
        lead.setEmail(updateRequest.getEmail());
        lead.setPhone(updateRequest.getPhone());
        lead.setCourseInterest(updateRequest.getCourseInterest());
        lead.setSource(updateRequest.getSource());
        lead.setStatus(updateRequest.getStatus());
        lead.setNotes(updateRequest.getNotes());
        lead.setNextFollowUpDate(updateRequest.getNextFollowUpDate());
        
        // Update assigned counsellor if provided
        if (updateRequest.getAssignedCounsellorId() != null) {
            Employee counsellor = employeeRepository.findById(updateRequest.getAssignedCounsellorId())
                .orElseThrow(() -> new ResourceNotFoundException("Counsellor not found with id: " + updateRequest.getAssignedCounsellorId()));
            
            if (!counsellor.getRole().equals(Employee.EmployeeRole.COUNSELLOR) && !counsellor.getRole().equals(Employee.EmployeeRole.ADMIN)) {
                throw new ValidationException("Assigned employee must be a COUNSELLOR or ADMIN");
            }
            
            lead.setAssignedCounsellor(counsellor);
        } else {
            lead.setAssignedCounsellor(null);
        }
        
        Lead savedLead = leadRepository.save(lead);
        return convertToResponseDTO(savedLead);
    }
    
    /**
     * Get lead by ID - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadResponseDTO getLeadById(UUID id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return convertToResponseDTO(lead);
    }
    
    /**
     * Get all leads with pagination, sorting, and filtering - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Page<LeadResponseDTO> getAllLeads(Lead.LeadStatus status, String source, String courseInterest, 
                                           UUID counsellorId, String searchTerm, Pageable pageable) {
        Page<Lead> leads = leadRepository.findLeadsWithFilters(status, source, courseInterest, counsellorId, searchTerm, pageable);
        return leads.map(this::convertToResponseDTO);
    }
    
    /**
     * Get leads by status with pagination - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Page<LeadResponseDTO> getLeadsByStatus(Lead.LeadStatus status, Pageable pageable) {
        Page<Lead> leads = leadRepository.findByStatus(status, pageable);
        return leads.map(this::convertToResponseDTO);
    }
    
    /**
     * Convert lead to student - Only ADMIN and COUNSELLOR can convert leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public StudentResponseDTO convertLeadToStudent(UUID leadId, LeadConversionRequestDTO conversionRequest) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        if (lead.getStatus() == Lead.LeadStatus.CONVERTED) {
            throw new LeadConversionException("Lead is already converted");
        }
        
        if (lead.getStatus() == Lead.LeadStatus.NOT_INTERESTED || lead.getStatus() == Lead.LeadStatus.LOST) {
            throw new LeadConversionException("Cannot convert lead with status: " + lead.getStatus());
        }
        
        // Create student from lead
        StudentCreateRequestDTO studentRequest = new StudentCreateRequestDTO();
        studentRequest.setFirstName(lead.getFirstName());
        studentRequest.setLastName(lead.getLastName());
        studentRequest.setEmail(lead.getEmail());
        studentRequest.setPhone(lead.getPhone());
        studentRequest.setEnrollmentDate(conversionRequest.getEnrollmentDate());
        studentRequest.setAddress(conversionRequest.getAddress());
        studentRequest.setDateOfBirth(conversionRequest.getDateOfBirth());
        studentRequest.setLeadId(lead.getId());
        studentRequest.setBatchId(conversionRequest.getBatchId());
        
        // Create the student
        StudentResponseDTO createdStudent = studentService.createStudent(studentRequest);
        
        // Update lead status
        lead.convertToStudent();
        leadRepository.save(lead);
        
        return createdStudent;
    }
    
    /**
     * Add follow-up to a lead - Only ADMIN and COUNSELLOR can add follow-ups
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadResponseDTO addFollowUp(UUID leadId, LeadFollowUpRequestDTO followUpRequest) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
        
        if (lead.getStatus() == Lead.LeadStatus.CONVERTED) {
            throw new ValidationException("Cannot add follow-up to converted lead");
        }
        
        lead.addFollowUp(followUpRequest.getNotes(), followUpRequest.getNextFollowUpDate());
        Lead savedLead = leadRepository.save(lead);
        return convertToResponseDTO(savedLead);
    }
    
    /**
     * Delete lead - Only ADMIN and COUNSELLOR can delete leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public void deleteLead(UUID id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        
        if (lead.getStatus() == Lead.LeadStatus.CONVERTED) {
            throw new ValidationException("Cannot delete converted lead");
        }
        
        leadRepository.deleteById(id);
    }
    
    /**
     * Get leads assigned to a counsellor with pagination - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public Page<LeadResponseDTO> getLeadsByCounsellor(UUID counsellorId, Pageable pageable) {
        Employee counsellor = employeeRepository.findById(counsellorId)
            .orElseThrow(() -> new ResourceNotFoundException("Counsellor not found with id: " + counsellorId));
        
        Page<Lead> leads = leadRepository.findByAssignedCounsellor(counsellor, pageable);
        return leads.map(this::convertToResponseDTO);
    }
    
    /**
     * Get leads requiring follow-up - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public List<LeadResponseDTO> getLeadsRequiringFollowUp() {
        List<Lead> leads = leadRepository.findLeadsRequiringFollowUp(LocalDateTime.now());
        return leads.stream().map(this::convertToResponseDTO).toList();
    }
    
    /**
     * Get leads without follow-up scheduled - Only ADMIN and COUNSELLOR can view leads
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public List<LeadResponseDTO> getLeadsWithoutFollowUp() {
        List<Lead> leads = leadRepository.findLeadsWithoutFollowUp();
        return leads.stream().map(this::convertToResponseDTO).toList();
    }
    
    /**
     * Get lead statistics - Only ADMIN and COUNSELLOR can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadStatsDTO getLeadStatistics() {
        LeadStatsDTO stats = new LeadStatsDTO();
        
        for (Lead.LeadStatus status : Lead.LeadStatus.values()) {
            long count = leadRepository.countByStatus(status);
            switch (status) {
                case NEW -> stats.setNewLeads(count);
                case CONTACTED -> stats.setContactedLeads(count);
                case INTERESTED -> stats.setInterestedLeads(count);
                case NOT_INTERESTED -> stats.setNotInterestedLeads(count);
                case CONVERTED -> stats.setConvertedLeads(count);
                case LOST -> stats.setLostLeads(count);
            }
        }
        
        stats.setTotalLeads(leadRepository.count());
        
        // Calculate conversion rate
        if (stats.getTotalLeads() > 0) {
            stats.setConversionRate((double) stats.getConvertedLeads() / stats.getTotalLeads() * 100);
        }
        
        return stats;
    }
    
    /**
     * Convert Lead entity to LeadResponseDTO
     */
    private LeadResponseDTO convertToResponseDTO(Lead lead) {
        LeadResponseDTO dto = new LeadResponseDTO();
        dto.setId(lead.getId());
        dto.setFirstName(lead.getFirstName());
        dto.setLastName(lead.getLastName());
        dto.setFullName(lead.getFullName());
        dto.setEmail(lead.getEmail());
        dto.setPhone(lead.getPhone());
        dto.setCourseInterest(lead.getCourseInterest());
        dto.setSource(lead.getSource());
        dto.setStatus(lead.getStatus());
        dto.setNotes(lead.getNotes());
        dto.setNextFollowUpDate(lead.getNextFollowUpDate());
        dto.setConvertedDate(lead.getConvertedDate());
        dto.setCreatedDate(lead.getCreatedDate());
        dto.setUpdatedDate(lead.getUpdatedDate());
        
        // Set assigned counsellor
        if (lead.getAssignedCounsellor() != null) {
            EmployeeBasicDTO counsellorDTO = new EmployeeBasicDTO();
            counsellorDTO.setId(lead.getAssignedCounsellor().getId());
            counsellorDTO.setEmployeeCode(lead.getAssignedCounsellor().getEmployeeCode());
            counsellorDTO.setFirstName(lead.getAssignedCounsellor().getFirstName());
            counsellorDTO.setLastName(lead.getAssignedCounsellor().getLastName());
            counsellorDTO.setFullName(lead.getAssignedCounsellor().getFullName());
            counsellorDTO.setRole(lead.getAssignedCounsellor().getRole());
            dto.setAssignedCounsellor(counsellorDTO);
        }
        
        // Set follow-ups
        if (lead.getFollowUps() != null && !lead.getFollowUps().isEmpty()) {
            List<LeadResponseDTO.FollowUpDTO> followUpDTOs = lead.getFollowUps().stream()
                .map(followUp -> new LeadResponseDTO.FollowUpDTO(
                    followUp.getDate(),
                    followUp.getNotes(),
                    followUp.getNextAction()
                ))
                .toList();
            dto.setFollowUps(followUpDTOs);
        }
        
        // Set converted students
        if (lead.getConvertedStudents() != null && !lead.getConvertedStudents().isEmpty()) {
            List<StudentBasicDTO> studentDTOs = lead.getConvertedStudents().stream()
                .map(student -> {
                    StudentBasicDTO studentDTO = new StudentBasicDTO();
                    studentDTO.setId(student.getId());
                    studentDTO.setEnrollmentNumber(student.getEnrollmentNumber());
                    studentDTO.setFirstName(student.getFirstName());
                    studentDTO.setLastName(student.getLastName());
                    studentDTO.setFullName(student.getFullName());
                    studentDTO.setStatus(student.getStatus());
                    return studentDTO;
                })
                .toList();
            dto.setConvertedStudents(studentDTOs);
        }
        
        return dto;
    }
    
    /**
     * Convert Student entity to StudentResponseDTO
     */
    private StudentResponseDTO convertStudentToResponseDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setEnrollmentNumber(student.getEnrollmentNumber());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setAddress(student.getAddress());
        dto.setStatus(student.getStatus());
        dto.setEnrollmentDate(student.getEnrollmentDate());
        dto.setGraduationDate(student.getGraduationDate());
        dto.setFinalGrade(student.getFinalGrade());
        dto.setCreatedDate(student.getCreatedDate());
        dto.setUpdatedDate(student.getUpdatedDate());
        
        // Set batch information if available
        if (student.getBatch() != null) {
            BatchBasicDTO batchDTO = new BatchBasicDTO();
            batchDTO.setId(student.getBatch().getId());
            batchDTO.setName(student.getBatch().getName());
            batchDTO.setStartDate(student.getBatch().getStartDate());
            batchDTO.setEndDate(student.getBatch().getEndDate());
            batchDTO.setStatus(student.getBatch().getStatus());
            dto.setBatch(batchDTO);
        }
        
        // Set lead information if available
        if (student.getLead() != null) {
            LeadBasicDTO leadDTO = new LeadBasicDTO();
            leadDTO.setId(student.getLead().getId());
            leadDTO.setFirstName(student.getLead().getFirstName());
            leadDTO.setLastName(student.getLead().getLastName());
            leadDTO.setFullName(student.getLead().getFullName());
            leadDTO.setStatus(student.getLead().getStatus());
            dto.setLead(leadDTO);
        }
        
        return dto;
    }
    
    /**
     * DTO for lead statistics
     */
    public static class LeadStatsDTO {
        private long totalLeads;
        private long newLeads;
        private long contactedLeads;
        private long interestedLeads;
        private long notInterestedLeads;
        private long convertedLeads;
        private long lostLeads;
        private double conversionRate;
        
        // Getters and Setters
        public long getTotalLeads() { return totalLeads; }
        public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }
        
        public long getNewLeads() { return newLeads; }
        public void setNewLeads(long newLeads) { this.newLeads = newLeads; }
        
        public long getContactedLeads() { return contactedLeads; }
        public void setContactedLeads(long contactedLeads) { this.contactedLeads = contactedLeads; }
        
        public long getInterestedLeads() { return interestedLeads; }
        public void setInterestedLeads(long interestedLeads) { this.interestedLeads = interestedLeads; }
        
        public long getNotInterestedLeads() { return notInterestedLeads; }
        public void setNotInterestedLeads(long notInterestedLeads) { this.notInterestedLeads = notInterestedLeads; }
        
        public long getConvertedLeads() { return convertedLeads; }
        public void setConvertedLeads(long convertedLeads) { this.convertedLeads = convertedLeads; }
        
        public long getLostLeads() { return lostLeads; }
        public void setLostLeads(long lostLeads) { this.lostLeads = lostLeads; }
        
        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
    }
}
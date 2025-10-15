package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.*;
import com.institute.management.exception.*;
import com.institute.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {
    
    @Mock
    private LeadRepository leadRepository;
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private StudentService studentService;
    
    @InjectMocks
    private LeadService leadService;
    
    private Lead testLead;
    private Employee testCounsellor;
    private LeadCreateRequestDTO createRequest;
    private LeadUpdateRequestDTO updateRequest;
    private UUID leadId;
    private UUID counsellorId;
    
    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();
        counsellorId = UUID.randomUUID();
        
        // Setup test counsellor
        testCounsellor = new Employee();
        testCounsellor.setId(counsellorId);
        testCounsellor.setEmployeeCode("EMP001");
        testCounsellor.setFirstName("John");
        testCounsellor.setLastName("Doe");
        testCounsellor.setEmail("john.doe@institute.com");
        testCounsellor.setRole(Employee.EmployeeRole.COUNSELLOR);
        testCounsellor.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        // Setup test lead
        testLead = new Lead();
        testLead.setId(leadId);
        testLead.setFirstName("Jane");
        testLead.setLastName("Smith");
        testLead.setEmail("jane.smith@email.com");
        testLead.setPhone("1234567890");
        testLead.setCourseInterest("Java Development");
        testLead.setSource("Website");
        testLead.setStatus(Lead.LeadStatus.NEW);
        testLead.setAssignedCounsellor(testCounsellor);
        testLead.setCreatedDate(LocalDateTime.now());
        
        // Setup create request
        createRequest = new LeadCreateRequestDTO();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setEmail("jane.smith@email.com");
        createRequest.setPhone("1234567890");
        createRequest.setCourseInterest("Java Development");
        createRequest.setSource("Website");
        createRequest.setAssignedCounsellorId(counsellorId);
        
        // Setup update request
        updateRequest = new LeadUpdateRequestDTO();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setEmail("jane.smith@email.com");
        updateRequest.setPhone("1234567890");
        updateRequest.setCourseInterest("Java Development");
        updateRequest.setSource("Website");
        updateRequest.setStatus(Lead.LeadStatus.CONTACTED);
        updateRequest.setAssignedCounsellorId(counsellorId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_Success() {
        // Given
        when(leadRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(leadRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(employeeRepository.findById(counsellorId)).thenReturn(Optional.of(testCounsellor));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        // When
        LeadResponseDTO result = leadService.createLead(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testLead.getFirstName(), result.getFirstName());
        assertEquals(testLead.getLastName(), result.getLastName());
        assertEquals(testLead.getEmail(), result.getEmail());
        assertEquals(testLead.getPhone(), result.getPhone());
        assertEquals(testLead.getCourseInterest(), result.getCourseInterest());
        assertEquals(testLead.getSource(), result.getSource());
        assertEquals(testLead.getStatus(), result.getStatus());
        
        verify(leadRepository).existsByEmail(createRequest.getEmail());
        verify(leadRepository).existsByPhone(createRequest.getPhone());
        verify(employeeRepository).findById(counsellorId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_DuplicateEmail_ThrowsException() {
        // Given
        when(leadRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> leadService.createLead(createRequest));
        
        verify(leadRepository).existsByEmail(createRequest.getEmail());
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_DuplicatePhone_ThrowsException() {
        // Given
        when(leadRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(leadRepository.existsByPhone(createRequest.getPhone())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> leadService.createLead(createRequest));
        
        verify(leadRepository).existsByEmail(createRequest.getEmail());
        verify(leadRepository).existsByPhone(createRequest.getPhone());
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_InvalidCounsellor_ThrowsException() {
        // Given
        when(leadRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(leadRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(employeeRepository.findById(counsellorId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> leadService.createLead(createRequest));
        
        verify(employeeRepository).findById(counsellorId);
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_NonCounsellorEmployee_ThrowsException() {
        // Given
        Employee nonCounsellor = new Employee();
        nonCounsellor.setId(counsellorId);
        nonCounsellor.setRole(Employee.EmployeeRole.FACULTY);
        
        when(leadRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(leadRepository.existsByPhone(createRequest.getPhone())).thenReturn(false);
        when(employeeRepository.findById(counsellorId)).thenReturn(Optional.of(nonCounsellor));
        
        // When & Then
        assertThrows(ValidationException.class, () -> leadService.createLead(createRequest));
        
        verify(employeeRepository).findById(counsellorId);
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void updateLead_Success() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(leadRepository.existsByPhone(updateRequest.getPhone())).thenReturn(false);
        when(employeeRepository.findById(counsellorId)).thenReturn(Optional.of(testCounsellor));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        // When
        LeadResponseDTO result = leadService.updateLead(leadId, updateRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getFirstName(), result.getFirstName());
        assertEquals(updateRequest.getStatus(), result.getStatus());
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void updateLead_LeadNotFound_ThrowsException() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> leadService.updateLead(leadId, updateRequest));
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void updateLead_ConvertedLead_ThrowsException() {
        // Given
        testLead.setStatus(Lead.LeadStatus.CONVERTED);
        updateRequest.setStatus(Lead.LeadStatus.INTERESTED);
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When & Then
        assertThrows(ValidationException.class, () -> leadService.updateLead(leadId, updateRequest));
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadById_Success() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When
        LeadResponseDTO result = leadService.getLeadById(leadId);
        
        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());
        assertEquals(testLead.getFirstName(), result.getFirstName());
        assertEquals(testLead.getLastName(), result.getLastName());
        
        verify(leadRepository).findById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadById_NotFound_ThrowsException() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> leadService.getLeadById(leadId));
        
        verify(leadRepository).findById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getAllLeads_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Lead> leads = Arrays.asList(testLead);
        Page<Lead> leadPage = new PageImpl<>(leads, pageable, 1);
        
        when(leadRepository.findLeadsWithFilters(any(), any(), any(), any(), any(), eq(pageable)))
            .thenReturn(leadPage);
        
        // When
        Page<LeadResponseDTO> result = leadService.getAllLeads(null, null, null, null, null, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testLead.getId(), result.getContent().get(0).getId());
        
        verify(leadRepository).findLeadsWithFilters(any(), any(), any(), any(), any(), eq(pageable));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsByStatus_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Lead> leads = Arrays.asList(testLead);
        Page<Lead> leadPage = new PageImpl<>(leads, pageable, 1);
        
        when(leadRepository.findByStatus(Lead.LeadStatus.NEW, pageable)).thenReturn(leadPage);
        
        // When
        Page<LeadResponseDTO> result = leadService.getLeadsByStatus(Lead.LeadStatus.NEW, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Lead.LeadStatus.NEW, result.getContent().get(0).getStatus());
        
        verify(leadRepository).findByStatus(Lead.LeadStatus.NEW, pageable);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void convertLeadToStudent_Success() {
        // Given
        LeadConversionRequestDTO conversionRequest = new LeadConversionRequestDTO();
        conversionRequest.setEnrollmentDate(LocalDate.now());
        
        StudentResponseDTO studentResponse = new StudentResponseDTO();
        studentResponse.setId(UUID.randomUUID());
        studentResponse.setEnrollmentNumber("STU001");
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(studentService.createStudent(any(Student.class))).thenReturn(new Student());
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        // When
        StudentResponseDTO result = leadService.convertLeadToStudent(leadId, conversionRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(studentResponse.getId(), result.getId());
        assertEquals(studentResponse.getEnrollmentNumber(), result.getEnrollmentNumber());
        
        verify(leadRepository).findById(leadId);
        verify(studentService).createStudent(any(Student.class));
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void convertLeadToStudent_AlreadyConverted_ThrowsException() {
        // Given
        testLead.setStatus(Lead.LeadStatus.CONVERTED);
        LeadConversionRequestDTO conversionRequest = new LeadConversionRequestDTO();
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When & Then
        assertThrows(LeadConversionException.class, () -> leadService.convertLeadToStudent(leadId, conversionRequest));
        
        verify(leadRepository).findById(leadId);
        verify(studentService, never()).createStudent(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void convertLeadToStudent_NotInterestedLead_ThrowsException() {
        // Given
        testLead.setStatus(Lead.LeadStatus.NOT_INTERESTED);
        LeadConversionRequestDTO conversionRequest = new LeadConversionRequestDTO();
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When & Then
        assertThrows(LeadConversionException.class, () -> leadService.convertLeadToStudent(leadId, conversionRequest));
        
        verify(leadRepository).findById(leadId);
        verify(studentService, never()).createStudent(any(Student.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void addFollowUp_Success() {
        // Given
        LeadFollowUpRequestDTO followUpRequest = new LeadFollowUpRequestDTO();
        followUpRequest.setNotes("Called customer, interested in course");
        followUpRequest.setNextFollowUpDate(LocalDateTime.now().plusDays(3));
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);
        
        // When
        LeadResponseDTO result = leadService.addFollowUp(leadId, followUpRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void addFollowUp_ConvertedLead_ThrowsException() {
        // Given
        testLead.setStatus(Lead.LeadStatus.CONVERTED);
        LeadFollowUpRequestDTO followUpRequest = new LeadFollowUpRequestDTO();
        followUpRequest.setNotes("Follow up notes");
        
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When & Then
        assertThrows(ValidationException.class, () -> leadService.addFollowUp(leadId, followUpRequest));
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository, never()).save(any(Lead.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void deleteLead_Success() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        doNothing().when(leadRepository).deleteById(leadId);
        
        // When
        leadService.deleteLead(leadId);
        
        // Then
        verify(leadRepository).findById(leadId);
        verify(leadRepository).deleteById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void deleteLead_NotFound_ThrowsException() {
        // Given
        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> leadService.deleteLead(leadId));
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository, never()).deleteById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void deleteLead_ConvertedLead_ThrowsException() {
        // Given
        testLead.setStatus(Lead.LeadStatus.CONVERTED);
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(testLead));
        
        // When & Then
        assertThrows(ValidationException.class, () -> leadService.deleteLead(leadId));
        
        verify(leadRepository).findById(leadId);
        verify(leadRepository, never()).deleteById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsByCounsellor_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Lead> leads = Arrays.asList(testLead);
        Page<Lead> leadPage = new PageImpl<>(leads, pageable, 1);
        
        when(employeeRepository.findById(counsellorId)).thenReturn(Optional.of(testCounsellor));
        when(leadRepository.findByAssignedCounsellor(testCounsellor, pageable)).thenReturn(leadPage);
        
        // When
        Page<LeadResponseDTO> result = leadService.getLeadsByCounsellor(counsellorId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testLead.getId(), result.getContent().get(0).getId());
        
        verify(employeeRepository).findById(counsellorId);
        verify(leadRepository).findByAssignedCounsellor(testCounsellor, pageable);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsRequiringFollowUp_Success() {
        // Given
        List<Lead> leads = Arrays.asList(testLead);
        when(leadRepository.findLeadsRequiringFollowUp(any(LocalDateTime.class))).thenReturn(leads);
        
        // When
        List<LeadResponseDTO> result = leadService.getLeadsRequiringFollowUp();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLead.getId(), result.get(0).getId());
        
        verify(leadRepository).findLeadsRequiringFollowUp(any(LocalDateTime.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsWithoutFollowUp_Success() {
        // Given
        List<Lead> leads = Arrays.asList(testLead);
        when(leadRepository.findLeadsWithoutFollowUp()).thenReturn(leads);
        
        // When
        List<LeadResponseDTO> result = leadService.getLeadsWithoutFollowUp();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLead.getId(), result.get(0).getId());
        
        verify(leadRepository).findLeadsWithoutFollowUp();
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadStatistics_Success() {
        // Given
        when(leadRepository.countByStatus(Lead.LeadStatus.NEW)).thenReturn(5L);
        when(leadRepository.countByStatus(Lead.LeadStatus.CONTACTED)).thenReturn(3L);
        when(leadRepository.countByStatus(Lead.LeadStatus.INTERESTED)).thenReturn(2L);
        when(leadRepository.countByStatus(Lead.LeadStatus.NOT_INTERESTED)).thenReturn(1L);
        when(leadRepository.countByStatus(Lead.LeadStatus.CONVERTED)).thenReturn(4L);
        when(leadRepository.countByStatus(Lead.LeadStatus.LOST)).thenReturn(1L);
        when(leadRepository.count()).thenReturn(16L);
        
        // When
        LeadService.LeadStatsDTO result = leadService.getLeadStatistics();
        
        // Then
        assertNotNull(result);
        assertEquals(16L, result.getTotalLeads());
        assertEquals(5L, result.getNewLeads());
        assertEquals(3L, result.getContactedLeads());
        assertEquals(2L, result.getInterestedLeads());
        assertEquals(1L, result.getNotInterestedLeads());
        assertEquals(4L, result.getConvertedLeads());
        assertEquals(1L, result.getLostLeads());
        assertEquals(25.0, result.getConversionRate(), 0.01); // 4/16 * 100 = 25%
        
        verify(leadRepository, times(6)).countByStatus(any(Lead.LeadStatus.class));
        verify(leadRepository).count();
    }
}
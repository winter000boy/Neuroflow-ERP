package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.*;
import com.institute.management.entity.Lead;
import com.institute.management.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
class LeadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LeadService leadService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private LeadResponseDTO leadResponseDTO;
    private LeadCreateRequestDTO createRequestDTO;
    private LeadUpdateRequestDTO updateRequestDTO;
    private UUID leadId;
    private UUID counsellorId;
    
    @BeforeEach
    void setUp() {
        leadId = UUID.randomUUID();
        counsellorId = UUID.randomUUID();
        
        // Setup response DTO
        leadResponseDTO = new LeadResponseDTO();
        leadResponseDTO.setId(leadId);
        leadResponseDTO.setFirstName("Jane");
        leadResponseDTO.setLastName("Smith");
        leadResponseDTO.setFullName("Jane Smith");
        leadResponseDTO.setEmail("jane.smith@email.com");
        leadResponseDTO.setPhone("1234567890");
        leadResponseDTO.setCourseInterest("Java Development");
        leadResponseDTO.setSource("Website");
        leadResponseDTO.setStatus(Lead.LeadStatus.NEW);
        leadResponseDTO.setCreatedDate(LocalDateTime.now());
        
        // Setup create request DTO
        createRequestDTO = new LeadCreateRequestDTO();
        createRequestDTO.setFirstName("Jane");
        createRequestDTO.setLastName("Smith");
        createRequestDTO.setEmail("jane.smith@email.com");
        createRequestDTO.setPhone("1234567890");
        createRequestDTO.setCourseInterest("Java Development");
        createRequestDTO.setSource("Website");
        createRequestDTO.setAssignedCounsellorId(counsellorId);
        
        // Setup update request DTO
        updateRequestDTO = new LeadUpdateRequestDTO();
        updateRequestDTO.setFirstName("Jane");
        updateRequestDTO.setLastName("Smith");
        updateRequestDTO.setEmail("jane.smith@email.com");
        updateRequestDTO.setPhone("1234567890");
        updateRequestDTO.setCourseInterest("Java Development");
        updateRequestDTO.setSource("Website");
        updateRequestDTO.setStatus(Lead.LeadStatus.CONTACTED);
        updateRequestDTO.setAssignedCounsellorId(counsellorId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_Success() throws Exception {
        // Given
        when(leadService.createLead(any(LeadCreateRequestDTO.class))).thenReturn(leadResponseDTO);
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(leadId.toString()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@email.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.courseInterest").value("Java Development"))
                .andExpect(jsonPath("$.source").value("Website"))
                .andExpect(jsonPath("$.status").value("NEW"));
        
        verify(leadService).createLead(any(LeadCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void createLead_InvalidInput_BadRequest() throws Exception {
        // Given
        LeadCreateRequestDTO invalidRequest = new LeadCreateRequestDTO();
        // Missing required fields
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(leadService, never()).createLead(any(LeadCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getAllLeads_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        Page<LeadResponseDTO> leadPage = new PageImpl<>(leads, PageRequest.of(0, 20), 1);
        
        when(leadService.getAllLeads(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(leadPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "createdDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(leadId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
        
        verify(leadService).getAllLeads(any(), any(), any(), any(), any(), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getAllLeads_WithFilters_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        Page<LeadResponseDTO> leadPage = new PageImpl<>(leads, PageRequest.of(0, 20), 1);
        
        when(leadService.getAllLeads(eq(Lead.LeadStatus.NEW), eq("Website"), eq("Java Development"), 
                eq(counsellorId), eq("Jane"), any(Pageable.class)))
                .thenReturn(leadPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads")
                .param("status", "NEW")
                .param("source", "Website")
                .param("courseInterest", "Java Development")
                .param("counsellorId", counsellorId.toString())
                .param("searchTerm", "Jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(leadId.toString()));
        
        verify(leadService).getAllLeads(eq(Lead.LeadStatus.NEW), eq("Website"), eq("Java Development"), 
                eq(counsellorId), eq("Jane"), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadById_Success() throws Exception {
        // Given
        when(leadService.getLeadById(leadId)).thenReturn(leadResponseDTO);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/{id}", leadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leadId.toString()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
        
        verify(leadService).getLeadById(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void updateLead_Success() throws Exception {
        // Given
        leadResponseDTO.setStatus(Lead.LeadStatus.CONTACTED);
        when(leadService.updateLead(eq(leadId), any(LeadUpdateRequestDTO.class))).thenReturn(leadResponseDTO);
        
        // When & Then
        mockMvc.perform(put("/api/v1/leads/{id}", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leadId.toString()))
                .andExpect(jsonPath("$.status").value("CONTACTED"));
        
        verify(leadService).updateLead(eq(leadId), any(LeadUpdateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void updateLead_InvalidInput_BadRequest() throws Exception {
        // Given
        LeadUpdateRequestDTO invalidRequest = new LeadUpdateRequestDTO();
        // Missing required fields
        
        // When & Then
        mockMvc.perform(put("/api/v1/leads/{id}", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(leadService, never()).updateLead(any(UUID.class), any(LeadUpdateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void deleteLead_Success() throws Exception {
        // Given
        doNothing().when(leadService).deleteLead(leadId);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/leads/{id}", leadId)
                .with(csrf()))
                .andExpect(status().isNoContent());
        
        verify(leadService).deleteLead(leadId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsByStatus_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        Page<LeadResponseDTO> leadPage = new PageImpl<>(leads, PageRequest.of(0, 20), 1);
        
        when(leadService.getLeadsByStatus(eq(Lead.LeadStatus.NEW), any(Pageable.class)))
                .thenReturn(leadPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/status/{status}", "NEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("NEW"));
        
        verify(leadService).getLeadsByStatus(eq(Lead.LeadStatus.NEW), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsByCounsellor_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        Page<LeadResponseDTO> leadPage = new PageImpl<>(leads, PageRequest.of(0, 20), 1);
        
        when(leadService.getLeadsByCounsellor(eq(counsellorId), any(Pageable.class)))
                .thenReturn(leadPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/counsellor/{counsellorId}", counsellorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(leadId.toString()));
        
        verify(leadService).getLeadsByCounsellor(eq(counsellorId), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void convertLeadToStudent_Success() throws Exception {
        // Given
        LeadConversionRequestDTO conversionRequest = new LeadConversionRequestDTO();
        conversionRequest.setEnrollmentDate(LocalDate.now());
        
        StudentResponseDTO studentResponse = new StudentResponseDTO();
        studentResponse.setId(UUID.randomUUID());
        studentResponse.setEnrollmentNumber("STU001");
        studentResponse.setFirstName("Jane");
        studentResponse.setLastName("Smith");
        
        when(leadService.convertLeadToStudent(eq(leadId), any(LeadConversionRequestDTO.class)))
                .thenReturn(studentResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads/{id}/convert", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conversionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentNumber").value("STU001"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
        
        verify(leadService).convertLeadToStudent(eq(leadId), any(LeadConversionRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void convertLeadToStudent_InvalidInput_BadRequest() throws Exception {
        // Given
        LeadConversionRequestDTO invalidRequest = new LeadConversionRequestDTO();
        // Missing required enrollment date
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads/{id}/convert", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(leadService, never()).convertLeadToStudent(any(UUID.class), any(LeadConversionRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void addFollowUp_Success() throws Exception {
        // Given
        LeadFollowUpRequestDTO followUpRequest = new LeadFollowUpRequestDTO();
        followUpRequest.setNotes("Called customer, interested in course");
        followUpRequest.setNextFollowUpDate(LocalDateTime.now().plusDays(3));
        
        when(leadService.addFollowUp(eq(leadId), any(LeadFollowUpRequestDTO.class)))
                .thenReturn(leadResponseDTO);
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads/{id}/follow-up", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leadId.toString()));
        
        verify(leadService).addFollowUp(eq(leadId), any(LeadFollowUpRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void addFollowUp_InvalidInput_BadRequest() throws Exception {
        // Given
        LeadFollowUpRequestDTO invalidRequest = new LeadFollowUpRequestDTO();
        // Missing required notes
        
        // When & Then
        mockMvc.perform(post("/api/v1/leads/{id}/follow-up", leadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(leadService, never()).addFollowUp(any(UUID.class), any(LeadFollowUpRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsRequiringFollowUp_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        when(leadService.getLeadsRequiringFollowUp()).thenReturn(leads);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/follow-up/required"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(leadId.toString()));
        
        verify(leadService).getLeadsRequiringFollowUp();
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadsWithoutFollowUp_Success() throws Exception {
        // Given
        List<LeadResponseDTO> leads = Arrays.asList(leadResponseDTO);
        when(leadService.getLeadsWithoutFollowUp()).thenReturn(leads);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/follow-up/missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(leadId.toString()));
        
        verify(leadService).getLeadsWithoutFollowUp();
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void getLeadStatistics_Success() throws Exception {
        // Given
        LeadService.LeadStatsDTO stats = new LeadService.LeadStatsDTO();
        stats.setTotalLeads(100);
        stats.setNewLeads(20);
        stats.setContactedLeads(15);
        stats.setInterestedLeads(10);
        stats.setNotInterestedLeads(5);
        stats.setConvertedLeads(25);
        stats.setLostLeads(25);
        stats.setConversionRate(25.0);
        
        when(leadService.getLeadStatistics()).thenReturn(stats);
        
        // When & Then
        mockMvc.perform(get("/api/v1/leads/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLeads").value(100))
                .andExpect(jsonPath("$.newLeads").value(20))
                .andExpect(jsonPath("$.contactedLeads").value(15))
                .andExpect(jsonPath("$.interestedLeads").value(10))
                .andExpect(jsonPath("$.notInterestedLeads").value(5))
                .andExpect(jsonPath("$.convertedLeads").value(25))
                .andExpect(jsonPath("$.lostLeads").value(25))
                .andExpect(jsonPath("$.conversionRate").value(25.0));
        
        verify(leadService).getLeadStatistics();
    }
    
    @Test
    void createLead_Unauthorized_Returns401() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isUnauthorized());
        
        verify(leadService, never()).createLead(any(LeadCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void createLead_Forbidden_Returns403() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isForbidden());
        
        verify(leadService, never()).createLead(any(LeadCreateRequestDTO.class));
    }
}
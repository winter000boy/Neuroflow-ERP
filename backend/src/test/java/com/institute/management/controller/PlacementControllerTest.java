package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.institute.management.dto.*;
import com.institute.management.entity.Placement;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.service.PlacementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlacementController.class)
class PlacementControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PlacementService placementService;
    
    private ObjectMapper objectMapper;
    private PlacementCreateRequestDTO createRequest;
    private PlacementUpdateRequestDTO updateRequest;
    private PlacementResponseDTO responseDTO;
    private UUID placementId;
    private UUID studentId;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        placementId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        
        // Setup create request
        createRequest = new PlacementCreateRequestDTO();
        createRequest.setStudentId(studentId);
        createRequest.setCompanyId(companyId);
        createRequest.setPosition("Software Engineer");
        createRequest.setSalary(new BigDecimal("500000"));
        createRequest.setPlacementDate(LocalDate.now());
        createRequest.setJobType(Placement.JobType.FULL_TIME);
        createRequest.setEmploymentType(Placement.EmploymentType.PERMANENT);
        createRequest.setWorkLocation("Bangalore");
        createRequest.setProbationPeriodMonths(6);
        createRequest.setJoiningDate(LocalDate.now().plusDays(30));
        
        // Setup update request
        updateRequest = new PlacementUpdateRequestDTO();
        updateRequest.setStudentId(studentId);
        updateRequest.setCompanyId(companyId);
        updateRequest.setPosition("Senior Software Engineer");
        updateRequest.setSalary(new BigDecimal("600000"));
        updateRequest.setPlacementDate(LocalDate.now());
        updateRequest.setStatus(Placement.PlacementStatus.PLACED);
        updateRequest.setJobType(Placement.JobType.FULL_TIME);
        updateRequest.setEmploymentType(Placement.EmploymentType.PERMANENT);
        updateRequest.setWorkLocation("Bangalore");
        updateRequest.setProbationPeriodMonths(6);
        updateRequest.setJoiningDate(LocalDate.now().plusDays(30));
        
        // Setup response DTO
        responseDTO = new PlacementResponseDTO();
        responseDTO.setId(placementId);
        responseDTO.setPosition("Software Engineer");
        responseDTO.setSalary(new BigDecimal("500000"));
        responseDTO.setPlacementDate(LocalDate.now());
        responseDTO.setStatus(Placement.PlacementStatus.PLACED);
        responseDTO.setJobType(Placement.JobType.FULL_TIME);
        responseDTO.setEmploymentType(Placement.EmploymentType.PERMANENT);
        responseDTO.setWorkLocation("Bangalore");
        responseDTO.setProbationPeriodMonths(6);
        responseDTO.setJoiningDate(LocalDate.now().plusDays(30));
        responseDTO.setCreatedDate(LocalDateTime.now());
        responseDTO.setUpdatedDate(LocalDateTime.now());
        responseDTO.setIsActive(true);
        responseDTO.setIsInProbation(false);
        responseDTO.setTenureInMonths(0L);
        
        // Setup student and company basic DTOs
        StudentBasicDTO studentDTO = new StudentBasicDTO();
        studentDTO.setId(studentId);
        studentDTO.setEnrollmentNumber("STU001");
        studentDTO.setFirstName("John");
        studentDTO.setLastName("Doe");
        studentDTO.setEmail("john.doe@example.com");
        responseDTO.setStudent(studentDTO);
        
        CompanyBasicDTO companyDTO = new CompanyBasicDTO();
        companyDTO.setId(companyId);
        companyDTO.setName("Tech Corp");
        companyDTO.setIndustry("Technology");
        companyDTO.setContactPerson("Jane Smith");
        companyDTO.setEmail("contact@techcorp.com");
        responseDTO.setCompany(companyDTO);
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createPlacement_Success() throws Exception {
        when(placementService.createPlacement(any(PlacementCreateRequestDTO.class)))
            .thenReturn(responseDTO);
        
        mockMvc.perform(post("/api/v1/placements")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(placementId.toString()))
                .andExpect(jsonPath("$.position").value("Software Engineer"))
                .andExpect(jsonPath("$.salary").value(500000))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.student.firstName").value("John"))
                .andExpect(jsonPath("$.company.name").value("Tech Corp"));
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void createPlacement_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/placements")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createPlacement_InvalidData() throws Exception {
        createRequest.setPosition(""); // Invalid empty position
        
        mockMvc.perform(post("/api/v1/placements")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementById_Success() throws Exception {
        when(placementService.getPlacementById(placementId))
            .thenReturn(responseDTO);
        
        mockMvc.perform(get("/api/v1/placements/{id}", placementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placementId.toString()))
                .andExpect(jsonPath("$.position").value("Software Engineer"))
                .andExpect(jsonPath("$.student.firstName").value("John"))
                .andExpect(jsonPath("$.company.name").value("Tech Corp"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementById_NotFound() throws Exception {
        when(placementService.getPlacementById(placementId))
            .thenThrow(new ResourceNotFoundException("Placement", "id", placementId));
        
        mockMvc.perform(get("/api/v1/placements/{id}", placementId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updatePlacement_Success() throws Exception {
        responseDTO.setPosition("Senior Software Engineer");
        responseDTO.setSalary(new BigDecimal("600000"));
        
        when(placementService.updatePlacement(eq(placementId), any(PlacementUpdateRequestDTO.class)))
            .thenReturn(responseDTO);
        
        mockMvc.perform(put("/api/v1/placements/{id}", placementId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Senior Software Engineer"))
                .andExpect(jsonPath("$.salary").value(600000));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlacement_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/placements/{id}", placementId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void deletePlacement_AccessDenied() throws Exception {
        mockMvc.perform(delete("/api/v1/placements/{id}", placementId)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllPlacements_Success() throws Exception {
        Page<PlacementResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        
        when(placementService.getAllPlacements(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(page);
        
        mockMvc.perform(get("/api/v1/placements")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(placementId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updatePlacementStatus_Success() throws Exception {
        responseDTO.setStatus(Placement.PlacementStatus.RESIGNED);
        
        when(placementService.updatePlacementStatus(placementId, Placement.PlacementStatus.RESIGNED))
            .thenReturn(responseDTO);
        
        mockMvc.perform(put("/api/v1/placements/{id}/status", placementId)
                .with(csrf())
                .param("status", "RESIGNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESIGNED"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementsByStudent_Success() throws Exception {
        when(placementService.getPlacementsByStudent(studentId))
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/placements/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(placementId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementsByCompany_Success() throws Exception {
        Page<PlacementResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        
        when(placementService.getPlacementsByCompany(eq(companyId), any()))
            .thenReturn(page);
        
        mockMvc.perform(get("/api/v1/placements/company/{companyId}", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(placementId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getRecentPlacements_Success() throws Exception {
        Page<PlacementResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 10), 1);
        
        when(placementService.getRecentPlacements(any()))
            .thenReturn(page);
        
        mockMvc.perform(get("/api/v1/placements/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(placementId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getActivePlacements_Success() throws Exception {
        when(placementService.getActivePlacements())
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/placements/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(placementId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementStatistics_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlacements", 100L);
        stats.put("activePlacements", 80L);
        stats.put("placementRate", 85.5);
        
        when(placementService.getPlacementStatistics())
            .thenReturn(stats);
        
        mockMvc.perform(get("/api/v1/placements/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPlacements").value(100))
                .andExpect(jsonPath("$.activePlacements").value(80))
                .andExpect(jsonPath("$.placementRate").value(85.5));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getSalaryStatistics_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageSalary", new BigDecimal("550000"));
        stats.put("minSalary", new BigDecimal("300000"));
        stats.put("maxSalary", new BigDecimal("1200000"));
        
        when(placementService.getSalaryStatistics())
            .thenReturn(stats);
        
        mockMvc.perform(get("/api/v1/placements/statistics/salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary").value(550000))
                .andExpect(jsonPath("$.minSalary").value(300000))
                .andExpect(jsonPath("$.maxSalary").value(1200000));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementTrends_Success() throws Exception {
        List<Map<String, Object>> trends = Arrays.asList(
            Map.of("year", 2024, "month", 1, "count", 15L),
            Map.of("year", 2024, "month", 2, "count", 20L)
        );
        
        when(placementService.getPlacementTrends(any(LocalDate.class)))
            .thenReturn(trends);
        
        mockMvc.perform(get("/api/v1/placements/statistics/trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].year").value(2024))
                .andExpect(jsonPath("$[0].month").value(1))
                .andExpect(jsonPath("$[0].count").value(15));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPlacementRateByCourse_Success() throws Exception {
        List<Map<String, Object>> rates = Arrays.asList(
            Map.of("courseName", "Java Full Stack", "totalGraduates", 50L, "placedStudents", 45L, "placementRate", 90.0)
        );
        
        when(placementService.getPlacementRateByCourse())
            .thenReturn(rates);
        
        mockMvc.perform(get("/api/v1/placements/statistics/placement-rate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].courseName").value("Java Full Stack"))
                .andExpect(jsonPath("$[0].placementRate").value(90.0));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyPerformance_Success() throws Exception {
        List<Map<String, Object>> performance = Arrays.asList(
            Map.of("companyName", "Tech Corp", "placementCount", 25L, "averageSalary", new BigDecimal("600000"))
        );
        
        when(placementService.getCompanyPerformanceStats())
            .thenReturn(performance);
        
        mockMvc.perform(get("/api/v1/placements/statistics/company-performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].companyName").value("Tech Corp"))
                .andExpect(jsonPath("$[0].placementCount").value(25));
    }
}
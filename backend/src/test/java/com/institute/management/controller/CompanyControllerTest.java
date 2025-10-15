package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.institute.management.dto.*;
import com.institute.management.entity.Company;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.service.CompanyService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CompanyService companyService;
    
    private ObjectMapper objectMapper;
    private CompanyCreateRequestDTO createRequest;
    private CompanyUpdateRequestDTO updateRequest;
    private CompanyResponseDTO responseDTO;
    private UUID companyId;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        companyId = UUID.randomUUID();
        
        // Setup create request
        createRequest = new CompanyCreateRequestDTO();
        createRequest.setName("Tech Corp");
        createRequest.setIndustry("Technology");
        createRequest.setContactPerson("Jane Smith");
        createRequest.setEmail("contact@techcorp.com");
        createRequest.setPhone("+1234567890");
        createRequest.setAddress("123 Tech Street, Silicon Valley");
        createRequest.setPartnershipDate(LocalDate.now());
        
        // Setup update request
        updateRequest = new CompanyUpdateRequestDTO();
        updateRequest.setName("Tech Corp Ltd");
        updateRequest.setIndustry("Information Technology");
        updateRequest.setContactPerson("Jane Smith");
        updateRequest.setEmail("contact@techcorp.com");
        updateRequest.setPhone("+1234567890");
        updateRequest.setAddress("123 Tech Street, Silicon Valley");
        updateRequest.setPartnershipDate(LocalDate.now());
        updateRequest.setStatus(Company.CompanyStatus.ACTIVE);
        
        // Setup response DTO
        responseDTO = new CompanyResponseDTO();
        responseDTO.setId(companyId);
        responseDTO.setName("Tech Corp");
        responseDTO.setIndustry("Technology");
        responseDTO.setContactPerson("Jane Smith");
        responseDTO.setEmail("contact@techcorp.com");
        responseDTO.setPhone("+1234567890");
        responseDTO.setAddress("123 Tech Street, Silicon Valley");
        responseDTO.setPartnershipDate(LocalDate.now());
        responseDTO.setStatus(Company.CompanyStatus.ACTIVE);
        responseDTO.setCreatedDate(LocalDateTime.now());
        responseDTO.setUpdatedDate(LocalDateTime.now());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_Success() throws Exception {
        when(companyService.createCompany(any(CompanyCreateRequestDTO.class)))
            .thenReturn(responseDTO);
        
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(companyId.toString()))
                .andExpect(jsonPath("$.name").value("Tech Corp"))
                .andExpect(jsonPath("$.industry").value("Technology"))
                .andExpect(jsonPath("$.contactPerson").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("contact@techcorp.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void createCompany_AccessDenied() throws Exception {
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_DuplicateName() throws Exception {
        when(companyService.createCompany(any(CompanyCreateRequestDTO.class)))
            .thenThrow(new DuplicateResourceException("Company with name 'Tech Corp' already exists"));
        
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void createCompany_InvalidData() throws Exception {
        createRequest.setName(""); // Invalid empty name
        
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyById_Success() throws Exception {
        when(companyService.getCompanyById(companyId))
            .thenReturn(responseDTO);
        
        mockMvc.perform(get("/api/v1/companies/{id}", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(companyId.toString()))
                .andExpect(jsonPath("$.name").value("Tech Corp"))
                .andExpect(jsonPath("$.industry").value("Technology"))
                .andExpect(jsonPath("$.contactPerson").value("Jane Smith"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyById_NotFound() throws Exception {
        when(companyService.getCompanyById(companyId))
            .thenThrow(new ResourceNotFoundException("Company", "id", companyId));
        
        mockMvc.perform(get("/api/v1/companies/{id}", companyId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updateCompany_Success() throws Exception {
        responseDTO.setName("Tech Corp Ltd");
        responseDTO.setIndustry("Information Technology");
        
        when(companyService.updateCompany(eq(companyId), any(CompanyUpdateRequestDTO.class)))
            .thenReturn(responseDTO);
        
        mockMvc.perform(put("/api/v1/companies/{id}", companyId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tech Corp Ltd"))
                .andExpect(jsonPath("$.industry").value("Information Technology"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/companies/{id}", companyId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void deleteCompany_AccessDenied() throws Exception {
        mockMvc.perform(delete("/api/v1/companies/{id}", companyId)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getAllCompanies_Success() throws Exception {
        Page<CompanyResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        
        when(companyService.getAllCompanies(any(), any(), any(), any(), any(), any()))
            .thenReturn(page);
        
        mockMvc.perform(get("/api/v1/companies")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(companyId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void updateCompanyStatus_Success() throws Exception {
        responseDTO.setStatus(Company.CompanyStatus.INACTIVE);
        
        when(companyService.updateCompanyStatus(companyId, Company.CompanyStatus.INACTIVE))
            .thenReturn(responseDTO);
        
        mockMvc.perform(put("/api/v1/companies/{id}/status", companyId)
                .with(csrf())
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getActiveCompanies_Success() throws Exception {
        when(companyService.getActiveCompanies())
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesByIndustry_Success() throws Exception {
        Page<CompanyResponseDTO> page = new PageImpl<>(Arrays.asList(responseDTO), PageRequest.of(0, 20), 1);
        
        when(companyService.getCompaniesByIndustry(eq("Technology"), any()))
            .thenReturn(page);
        
        mockMvc.perform(get("/api/v1/companies/industry/{industry}", "Technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].industry").value("Technology"));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesWithPlacements_Success() throws Exception {
        when(companyService.getCompaniesWithPlacements())
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/with-placements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesWithoutPlacements_Success() throws Exception {
        when(companyService.getCompaniesWithoutPlacements())
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/without-placements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getRecentPartners_Success() throws Exception {
        when(companyService.getRecentPartners(6))
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/recent-partners")
                .param("months", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getLongTermPartners_Success() throws Exception {
        when(companyService.getLongTermPartners(2))
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/long-term-partners")
                .param("years", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getActiveHiringCompanies_Success() throws Exception {
        when(companyService.getActiveHiringCompanies(12))
            .thenReturn(Arrays.asList(responseDTO));
        
        mockMvc.perform(get("/api/v1/companies/active-hiring")
                .param("months", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(companyId.toString()));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getTopHiringCompanies_Success() throws Exception {
        List<Map<String, Object>> topHiring = Arrays.asList(
            Map.of("companyId", companyId, "companyName", "Tech Corp", "industry", "Technology", "placementCount", 25L)
        );
        
        when(companyService.getTopHiringCompanies(any(LocalDate.class), any(LocalDate.class), eq(10)))
            .thenReturn(topHiring);
        
        mockMvc.perform(get("/api/v1/companies/top-hiring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].companyName").value("Tech Corp"))
                .andExpect(jsonPath("$[0].placementCount").value(25));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompanyStatistics_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCompanies", 50L);
        stats.put("activeCompanies", 45L);
        stats.put("inactiveCompanies", 3L);
        stats.put("blacklistedCompanies", 2L);
        stats.put("distinctIndustries", 8L);
        
        when(companyService.getCompanyStatistics())
            .thenReturn(stats);
        
        mockMvc.perform(get("/api/v1/companies/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCompanies").value(50))
                .andExpect(jsonPath("$.activeCompanies").value(45))
                .andExpect(jsonPath("$.distinctIndustries").value(8));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getIndustryDistribution_Success() throws Exception {
        List<Map<String, Object>> distribution = Arrays.asList(
            Map.of("industry", "Technology", "count", 20L),
            Map.of("industry", "Finance", "count", 15L)
        );
        
        when(companyService.getIndustryDistribution())
            .thenReturn(distribution);
        
        mockMvc.perform(get("/api/v1/companies/statistics/industry-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].industry").value("Technology"))
                .andExpect(jsonPath("$[0].count").value(20));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getPartnershipTrends_Success() throws Exception {
        List<Map<String, Object>> trends = Arrays.asList(
            Map.of("date", LocalDate.now().minusMonths(1), "count", 5L),
            Map.of("date", LocalDate.now(), "count", 8L)
        );
        
        when(companyService.getPartnershipTrends(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(trends);
        
        mockMvc.perform(get("/api/v1/companies/statistics/partnership-trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].count").value(5))
                .andExpect(jsonPath("$[1].count").value(8));
    }
    
    @Test
    @WithMockUser(roles = "PLACEMENT_OFFICER")
    void getCompaniesBySalaryOffered_Success() throws Exception {
        List<Map<String, Object>> salaryRanking = Arrays.asList(
            Map.of("companyId", companyId, "companyName", "Tech Corp", "industry", "Technology", 
                   "averageSalary", 650000, "placementCount", 15L)
        );
        
        when(companyService.getCompaniesBySalaryOffered(3))
            .thenReturn(salaryRanking);
        
        mockMvc.perform(get("/api/v1/companies/statistics/salary-ranking")
                .param("minPlacements", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].companyName").value("Tech Corp"))
                .andExpect(jsonPath("$[0].averageSalary").value(650000));
    }
}
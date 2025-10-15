package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.*;
import com.institute.management.entity.Employee;
import com.institute.management.service.EmployeeService;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EmployeeService employeeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private EmployeeResponseDTO employeeResponseDTO;
    private EmployeeCreateRequestDTO createRequestDTO;
    private EmployeeUpdateRequestDTO updateRequestDTO;
    private UUID employeeId;
    
    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        
        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(employeeId);
        employeeResponseDTO.setEmployeeCode("EMP001");
        employeeResponseDTO.setFirstName("John");
        employeeResponseDTO.setLastName("Doe");
        employeeResponseDTO.setFullName("John Doe");
        employeeResponseDTO.setEmail("john.doe@example.com");
        employeeResponseDTO.setPhone("1234567890");
        employeeResponseDTO.setDepartment("IT");
        employeeResponseDTO.setRole(Employee.EmployeeRole.FACULTY);
        employeeResponseDTO.setHireDate(LocalDate.now());
        employeeResponseDTO.setStatus(Employee.EmployeeStatus.ACTIVE);
        employeeResponseDTO.setCreatedDate(LocalDateTime.now());
        employeeResponseDTO.setUpdatedDate(LocalDateTime.now());
        
        createRequestDTO = new EmployeeCreateRequestDTO();
        createRequestDTO.setEmployeeCode("EMP001");
        createRequestDTO.setFirstName("John");
        createRequestDTO.setLastName("Doe");
        createRequestDTO.setEmail("john.doe@example.com");
        createRequestDTO.setPhone("1234567890");
        createRequestDTO.setDepartment("IT");
        createRequestDTO.setRole(Employee.EmployeeRole.FACULTY);
        createRequestDTO.setHireDate(LocalDate.now());
        
        updateRequestDTO = new EmployeeUpdateRequestDTO();
        updateRequestDTO.setEmployeeCode("EMP001");
        updateRequestDTO.setFirstName("Jane");
        updateRequestDTO.setLastName("Smith");
        updateRequestDTO.setEmail("jane.smith@example.com");
        updateRequestDTO.setPhone("1234567890");
        updateRequestDTO.setDepartment("IT");
        updateRequestDTO.setRole(Employee.EmployeeRole.FACULTY);
        updateRequestDTO.setHireDate(LocalDate.now());
        updateRequestDTO.setStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_Success() throws Exception {
        // Arrange
        when(employeeService.createEmployee(any(EmployeeCreateRequestDTO.class)))
            .thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(employeeId.toString()))
                .andExpect(jsonPath("$.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.role").value("FACULTY"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        
        verify(employeeService).createEmployee(any(EmployeeCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testCreateEmployee_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isForbidden());
        
        verify(employeeService, never()).createEmployee(any(EmployeeCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeeById_Success() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()))
                .andExpect(jsonPath("$.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
        
        verify(employeeService).getEmployeeById(employeeId);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEmployee_Success() throws Exception {
        // Arrange
        when(employeeService.updateEmployee(eq(employeeId), any(EmployeeUpdateRequestDTO.class)))
            .thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()));
        
        verify(employeeService).updateEmployee(eq(employeeId), any(EmployeeUpdateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testUpdateEmployee_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isForbidden());
        
        verify(employeeService, never()).updateEmployee(any(UUID.class), any(EmployeeUpdateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEmployee_Success() throws Exception {
        // Arrange
        doNothing().when(employeeService).deleteEmployee(employeeId);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId)
                .with(csrf()))
                .andExpect(status().isNoContent());
        
        verify(employeeService).deleteEmployee(employeeId);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testDeleteEmployee_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId)
                .with(csrf()))
                .andExpect(status().isForbidden());
        
        verify(employeeService, never()).deleteEmployee(any(UUID.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployees_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(employees);
        
        when(employeeService.getAllEmployees(any(Pageable.class), any(), any(), any(), any()))
            .thenReturn(employeePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "firstName")
                .param("sortDir", "asc")
                .param("status", "ACTIVE")
                .param("role", "FACULTY")
                .param("department", "IT")
                .param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(employeeId.toString()));
        
        verify(employeeService).getAllEmployees(any(Pageable.class), 
            eq(Employee.EmployeeStatus.ACTIVE), eq(Employee.EmployeeRole.FACULTY), eq("IT"), eq("John"));
    }
    
    @Test
    @WithMockUser(roles = "FACULTY")
    void testGetAllEmployees_FacultyAccess() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(employees);
        
        when(employeeService.getAllEmployees(any(Pageable.class), any(), any(), any(), any()))
            .thenReturn(employeePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk());
        
        verify(employeeService).getAllEmployees(any(Pageable.class), any(), any(), any(), any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeesByRole_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getEmployeesByRole(Employee.EmployeeRole.FACULTY)).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/role/{role}", "FACULTY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].role").value("FACULTY"));
        
        verify(employeeService).getEmployeesByRole(Employee.EmployeeRole.FACULTY);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeesByDepartment_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getEmployeesByDepartment("IT")).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/department/{department}", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].department").value("IT"));
        
        verify(employeeService).getEmployeesByDepartment("IT");
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeesByStatus_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getEmployeesByStatus(Employee.EmployeeStatus.ACTIVE)).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/status/{status}", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
        
        verify(employeeService).getEmployeesByStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetActiveEmployees_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getActiveEmployees()).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
        
        verify(employeeService).getActiveEmployees();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCounsellors_Success() throws Exception {
        // Arrange
        employeeResponseDTO.setRole(Employee.EmployeeRole.COUNSELLOR);
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getCounsellors()).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/counsellors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].role").value("COUNSELLOR"));
        
        verify(employeeService).getCounsellors();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetFaculty_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        when(employeeService.getFaculty()).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(employeeId.toString()))
                .andExpect(jsonPath("$[0].role").value("FACULTY"));
        
        verify(employeeService).getFaculty();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEmployeeRole_Success() throws Exception {
        // Arrange
        when(employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR))
            .thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}/role", employeeId)
                .with(csrf())
                .param("role", "COUNSELLOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()));
        
        verify(employeeService).updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR);
    }
    
    @Test
    @WithMockUser(roles = "COUNSELLOR")
    void testUpdateEmployeeRole_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}/role", employeeId)
                .with(csrf())
                .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
        
        verify(employeeService, never()).updateEmployeeRole(any(UUID.class), any(Employee.EmployeeRole.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEmployeeStatus_Success() throws Exception {
        // Arrange
        when(employeeService.updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE))
            .thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}/status", employeeId)
                .with(csrf())
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()));
        
        verify(employeeService).updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateEmployee_Success() throws Exception {
        // Arrange
        when(employeeService.deactivateEmployee(employeeId)).thenReturn(employeeResponseDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/employees/{id}/deactivate", employeeId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId.toString()));
        
        verify(employeeService).deactivateEmployee(employeeId);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchEmployees_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(employees);
        
        when(employeeService.searchEmployees(eq("John"), any(Pageable.class)))
            .thenReturn(employeePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/search")
                .param("searchTerm", "John")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "firstName")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(employeeId.toString()));
        
        verify(employeeService).searchEmployees(eq("John"), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeesByRoleAndDepartment_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(employees);
        
        when(employeeService.getEmployeesByRoleAndDepartment(
            eq(Employee.EmployeeRole.FACULTY), eq("IT"), any(Pageable.class)))
            .thenReturn(employeePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/filter")
                .param("role", "FACULTY")
                .param("department", "IT")
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "firstName")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(employeeId.toString()));
        
        verify(employeeService).getEmployeesByRoleAndDepartment(
            eq(Employee.EmployeeRole.FACULTY), eq("IT"), any(Pageable.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeesHiredInDateRange_Success() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> employeePage = new PageImpl<>(employees);
        
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        when(employeeService.getEmployeesHiredInDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
            .thenReturn(employeePage);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/employees/hired-between")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "20")
                .param("sortBy", "hireDate")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(employeeId.toString()));
        
        verify(employeeService).getEmployeesHiredInDateRange(eq(startDate), eq(endDate), any(Pageable.class));
    }
    
    @Test
    void testCreateEmployee_Unauthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isUnauthorized());
        
        verify(employeeService, never()).createEmployee(any(EmployeeCreateRequestDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_InvalidInput() throws Exception {
        // Arrange
        createRequestDTO.setFirstName(""); // Invalid - empty first name
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/employees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isBadRequest());
        
        verify(employeeService, never()).createEmployee(any(EmployeeCreateRequestDTO.class));
    }
}
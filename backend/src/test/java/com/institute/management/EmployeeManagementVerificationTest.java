package com.institute.management;

import com.institute.management.dto.*;
import com.institute.management.entity.Employee;
import com.institute.management.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple verification test to ensure Employee Management API is properly implemented
 */
@SpringBootTest
@ActiveProfiles("test")
class EmployeeManagementVerificationTest {
    
    @Test
    void testEmployeeServiceExists() {
        // This test verifies that the EmployeeService class exists and can be instantiated
        assertDoesNotThrow(() -> {
            EmployeeService service = new EmployeeService();
            assertNotNull(service);
        });
    }
    
    @Test
    void testEmployeeDTOsExist() {
        // This test verifies that all required DTOs exist and can be instantiated
        assertDoesNotThrow(() -> {
            EmployeeCreateRequestDTO createDTO = new EmployeeCreateRequestDTO();
            EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
            EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
            
            assertNotNull(createDTO);
            assertNotNull(updateDTO);
            assertNotNull(responseDTO);
        });
    }
    
    @Test
    void testEmployeeCreateRequestDTOValidation() {
        // Test that EmployeeCreateRequestDTO has all required fields
        EmployeeCreateRequestDTO dto = new EmployeeCreateRequestDTO();
        
        // Test setters and getters
        dto.setEmployeeCode("EMP001");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("1234567890");
        dto.setDepartment("IT");
        dto.setRole(Employee.EmployeeRole.FACULTY);
        dto.setHireDate(LocalDate.now());
        
        assertEquals("EMP001", dto.getEmployeeCode());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("IT", dto.getDepartment());
        assertEquals(Employee.EmployeeRole.FACULTY, dto.getRole());
        assertNotNull(dto.getHireDate());
    }
    
    @Test
    void testEmployeeUpdateRequestDTOValidation() {
        // Test that EmployeeUpdateRequestDTO has all required fields
        EmployeeUpdateRequestDTO dto = new EmployeeUpdateRequestDTO();
        
        // Test setters and getters
        dto.setEmployeeCode("EMP001");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setPhone("1234567890");
        dto.setDepartment("IT");
        dto.setRole(Employee.EmployeeRole.COUNSELLOR);
        dto.setHireDate(LocalDate.now());
        dto.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        assertEquals("EMP001", dto.getEmployeeCode());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("jane.smith@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("IT", dto.getDepartment());
        assertEquals(Employee.EmployeeRole.COUNSELLOR, dto.getRole());
        assertEquals(Employee.EmployeeStatus.ACTIVE, dto.getStatus());
        assertNotNull(dto.getHireDate());
    }
    
    @Test
    void testEmployeeResponseDTOValidation() {
        // Test that EmployeeResponseDTO has all required fields
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        
        // Test setters and getters for basic fields
        dto.setEmployeeCode("EMP001");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setFullName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("1234567890");
        dto.setDepartment("IT");
        dto.setRole(Employee.EmployeeRole.FACULTY);
        dto.setHireDate(LocalDate.now());
        dto.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        assertEquals("EMP001", dto.getEmployeeCode());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("John Doe", dto.getFullName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("IT", dto.getDepartment());
        assertEquals(Employee.EmployeeRole.FACULTY, dto.getRole());
        assertEquals(Employee.EmployeeStatus.ACTIVE, dto.getStatus());
        assertNotNull(dto.getHireDate());
    }
    
    @Test
    void testEmployeeRolesExist() {
        // Test that all required employee roles exist
        assertDoesNotThrow(() -> {
            Employee.EmployeeRole admin = Employee.EmployeeRole.ADMIN;
            Employee.EmployeeRole counsellor = Employee.EmployeeRole.COUNSELLOR;
            Employee.EmployeeRole faculty = Employee.EmployeeRole.FACULTY;
            Employee.EmployeeRole placementOfficer = Employee.EmployeeRole.PLACEMENT_OFFICER;
            Employee.EmployeeRole operations = Employee.EmployeeRole.OPERATIONS;
            
            assertNotNull(admin);
            assertNotNull(counsellor);
            assertNotNull(faculty);
            assertNotNull(placementOfficer);
            assertNotNull(operations);
        });
    }
    
    @Test
    void testEmployeeStatusesExist() {
        // Test that all required employee statuses exist
        assertDoesNotThrow(() -> {
            Employee.EmployeeStatus active = Employee.EmployeeStatus.ACTIVE;
            Employee.EmployeeStatus inactive = Employee.EmployeeStatus.INACTIVE;
            Employee.EmployeeStatus terminated = Employee.EmployeeStatus.TERMINATED;
            
            assertNotNull(active);
            assertNotNull(inactive);
            assertNotNull(terminated);
        });
    }
}
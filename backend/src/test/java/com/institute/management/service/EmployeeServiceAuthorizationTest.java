package com.institute.management.service;

import com.institute.management.dto.EmployeeCreateRequestDTO;
import com.institute.management.dto.EmployeeResponseDTO;
import com.institute.management.dto.EmployeeUpdateRequestDTO;
import com.institute.management.entity.Employee;
import com.institute.management.repository.EmployeeRepository;
import com.institute.management.security.AuthorizationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class EmployeeServiceAuthorizationTest extends AuthorizationTestBase {
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private EmployeeService employeeService;
    
    private Employee testEmployee;
    private EmployeeCreateRequestDTO createRequestDTO;
    private EmployeeUpdateRequestDTO updateRequestDTO;
    private UUID employeeId;
    
    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        testEmployee = new Employee();
        testEmployee.setId(employeeId);
        testEmployee.setEmployeeCode("EMP001");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setPhone("1234567890");
        testEmployee.setDepartment("IT");
        testEmployee.setRole(Employee.EmployeeRole.FACULTY);
        testEmployee.setHireDate(LocalDate.now());
        testEmployee.setStatus(Employee.EmployeeStatus.ACTIVE);
        
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
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreateEmployee() {
        when(employeeRepository.existsByEmployeeCode(any())).thenReturn(false);
        when(employeeRepository.existsByEmail(any())).thenReturn(false);
        when(employeeRepository.existsByPhone(any())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        EmployeeResponseDTO result = employeeService.createEmployee(createRequestDTO);
        
        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByEmployeeCode(any())).thenReturn(false);
        when(employeeRepository.existsByEmail(any())).thenReturn(false);
        when(employeeRepository.existsByPhone(any())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        EmployeeResponseDTO result = employeeService.updateEmployee(employeeId, updateRequestDTO);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotUpdateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployee(employeeId, updateRequestDTO);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployee(employeeId, updateRequestDTO);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateEmployeeRole() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        EmployeeResponseDTO result = employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotUpdateEmployeeRole() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.ADMIN);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateEmployeeStatus() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        EmployeeResponseDTO result = employeeService.updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotUpdateEmployeeStatus() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeactivateEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        EmployeeResponseDTO result = employeeService.deactivateEmployee(employeeId);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotDeactivateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.deactivateEmployee(employeeId);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanDeleteEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        assertDoesNotThrow(() -> {
            employeeService.deleteEmployee(employeeId);
        });
        
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotDeleteEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}
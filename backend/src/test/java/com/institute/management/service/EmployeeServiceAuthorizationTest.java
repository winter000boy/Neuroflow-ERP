package com.institute.management.service;

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
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanCreateEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        Employee result = employeeService.createEmployee(testEmployee);
        
        assertNotNull(result);
        verify(employeeRepository).save(testEmployee);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(testEmployee);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(testEmployee);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(testEmployee);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCannotCreateEmployee() {
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(testEmployee);
        });
        
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);
        
        assertTrue(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);
        
        assertTrue(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);
        
        assertTrue(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.PLACEMENT_OFFICER)
    void testPlacementOfficerCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);
        
        assertTrue(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.OPERATIONS)
    void testOperationsCanViewEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);
        
        assertTrue(result.isPresent());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Jane");
        updatedEmployee.setLastName("Smith");
        
        Employee result = employeeService.updateEmployee(employeeId, updatedEmployee);
        
        assertNotNull(result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.COUNSELLOR)
    void testCounsellorCannotUpdateEmployee() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Jane");
        
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployee(employeeId, updatedEmployee);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.FACULTY)
    void testFacultyCannotUpdateEmployee() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Jane");
        
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.updateEmployee(employeeId, updatedEmployee);
        });
        
        verify(employeeRepository, never()).findById(any(UUID.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    @WithMockUser(role = Employee.EmployeeRole.ADMIN)
    void testAdminCanUpdateEmployeeRole() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        Employee result = employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR);
        
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
        
        Employee result = employeeService.updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE);
        
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
        
        Employee result = employeeService.deactivateEmployee(employeeId);
        
        assertNotNull(result);
        assertEquals(Employee.EmployeeStatus.INACTIVE, result.getStatus());
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
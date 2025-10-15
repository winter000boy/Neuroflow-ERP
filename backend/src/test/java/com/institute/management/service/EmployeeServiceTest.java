package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.Employee;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.exception.ValidationException;
import com.institute.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    
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
        testEmployee.setCreatedDate(LocalDateTime.now());
        testEmployee.setUpdatedDate(LocalDateTime.now());
        
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
    void testCreateEmployee_Success() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(createRequestDTO.getEmployeeCode())).thenReturn(false);
        when(employeeRepository.existsByEmail(createRequestDTO.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhone(createRequestDTO.getPhone())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponseDTO result = employeeService.createEmployee(createRequestDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getEmployeeCode(), result.getEmployeeCode());
        assertEquals(testEmployee.getFirstName(), result.getFirstName());
        assertEquals(testEmployee.getLastName(), result.getLastName());
        assertEquals(testEmployee.getEmail(), result.getEmail());
        assertEquals(testEmployee.getPhone(), result.getPhone());
        assertEquals(testEmployee.getDepartment(), result.getDepartment());
        assertEquals(testEmployee.getRole(), result.getRole());
        assertEquals(testEmployee.getHireDate(), result.getHireDate());
        assertEquals(testEmployee.getStatus(), result.getStatus());
        
        verify(employeeRepository).existsByEmployeeCode(createRequestDTO.getEmployeeCode());
        verify(employeeRepository).existsByEmail(createRequestDTO.getEmail());
        verify(employeeRepository).existsByPhone(createRequestDTO.getPhone());
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testCreateEmployee_DuplicateEmployeeCode() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(createRequestDTO.getEmployeeCode())).thenReturn(true);
        
        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        assertEquals("Employee with code " + createRequestDTO.getEmployeeCode() + " already exists", exception.getMessage());
        verify(employeeRepository).existsByEmployeeCode(createRequestDTO.getEmployeeCode());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testCreateEmployee_DuplicateEmail() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(createRequestDTO.getEmployeeCode())).thenReturn(false);
        when(employeeRepository.existsByEmail(createRequestDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        assertEquals("Employee with email " + createRequestDTO.getEmail() + " already exists", exception.getMessage());
        verify(employeeRepository).existsByEmployeeCode(createRequestDTO.getEmployeeCode());
        verify(employeeRepository).existsByEmail(createRequestDTO.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testCreateEmployee_DuplicatePhone() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(createRequestDTO.getEmployeeCode())).thenReturn(false);
        when(employeeRepository.existsByEmail(createRequestDTO.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhone(createRequestDTO.getPhone())).thenReturn(true);
        
        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        assertEquals("Employee with phone " + createRequestDTO.getPhone() + " already exists", exception.getMessage());
        verify(employeeRepository).existsByEmployeeCode(createRequestDTO.getEmployeeCode());
        verify(employeeRepository).existsByEmail(createRequestDTO.getEmail());
        verify(employeeRepository).existsByPhone(createRequestDTO.getPhone());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testCreateEmployee_NullRole() {
        // Arrange
        createRequestDTO.setRole(null);
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            employeeService.createEmployee(createRequestDTO);
        });
        
        assertEquals("Employee role is required", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testGetEmployeeById_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        
        // Act
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getEmployeeCode(), result.getEmployeeCode());
        assertEquals(testEmployee.getFirstName(), result.getFirstName());
        assertEquals(testEmployee.getLastName(), result.getLastName());
        assertEquals(testEmployee.getEmail(), result.getEmail());
        
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    void testGetEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });
        
        assertEquals("Employee not found with id: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }
    
    @Test
    void testUpdateEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByEmployeeCode(updateRequestDTO.getEmployeeCode())).thenReturn(false);
        when(employeeRepository.existsByEmail(updateRequestDTO.getEmail())).thenReturn(false);
        when(employeeRepository.existsByPhone(updateRequestDTO.getPhone())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponseDTO result = employeeService.updateEmployee(employeeId, updateRequestDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployee_NotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.updateEmployee(employeeId, updateRequestDTO);
        });
        
        assertEquals("Employee not found with id: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployee_DuplicateEmployeeCode() {
        // Arrange
        updateRequestDTO.setEmployeeCode("EMP002");
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByEmployeeCode(updateRequestDTO.getEmployeeCode())).thenReturn(true);
        
        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            employeeService.updateEmployee(employeeId, updateRequestDTO);
        });
        
        assertEquals("Employee with code " + updateRequestDTO.getEmployeeCode() + " already exists", exception.getMessage());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).existsByEmployeeCode(updateRequestDTO.getEmployeeCode());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testDeleteEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        assertDoesNotThrow(() -> {
            employeeService.deleteEmployee(employeeId);
        });
        
        // Assert
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testDeleteEmployee_NotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });
        
        assertEquals("Employee not found with id: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testGetAllEmployees_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        Page<Employee> employeePage = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("firstName"));
        
        when(employeeRepository.findEmployeesWithFilters(any(), any(), any(), any(), eq(pageable)))
            .thenReturn(employeePage);
        
        // Act
        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(
            pageable, Employee.EmployeeStatus.ACTIVE, Employee.EmployeeRole.FACULTY, "IT", "John");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testEmployee.getId(), result.getContent().get(0).getId());
        
        verify(employeeRepository).findEmployeesWithFilters(
            Employee.EmployeeStatus.ACTIVE, Employee.EmployeeRole.FACULTY, "IT", "John", pageable);
    }
    
    @Test
    void testGetEmployeesByRole_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByRole(Employee.EmployeeRole.FACULTY)).thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getEmployeesByRole(Employee.EmployeeRole.FACULTY);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(testEmployee.getRole(), result.get(0).getRole());
        
        verify(employeeRepository).findByRole(Employee.EmployeeRole.FACULTY);
    }
    
    @Test
    void testGetEmployeesByDepartment_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByDepartment("IT")).thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getEmployeesByDepartment("IT");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(testEmployee.getDepartment(), result.get(0).getDepartment());
        
        verify(employeeRepository).findByDepartment("IT");
    }
    
    @Test
    void testGetEmployeesByStatus_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE)).thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getEmployeesByStatus(Employee.EmployeeStatus.ACTIVE);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(testEmployee.getStatus(), result.get(0).getStatus());
        
        verify(employeeRepository).findByStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    void testUpdateEmployeeRole_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponseDTO result = employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployeeRole_NotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.updateEmployeeRole(employeeId, Employee.EmployeeRole.COUNSELLOR);
        });
        
        assertEquals("Employee not found with id: " + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void testUpdateEmployeeStatus_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponseDTO result = employeeService.updateEmployeeStatus(employeeId, Employee.EmployeeStatus.INACTIVE);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testDeactivateEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        
        // Act
        EmployeeResponseDTO result = employeeService.deactivateEmployee(employeeId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void testGetActiveEmployees_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByStatus(Employee.EmployeeStatus.ACTIVE)).thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getActiveEmployees();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(Employee.EmployeeStatus.ACTIVE, result.get(0).getStatus());
        
        verify(employeeRepository).findByStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    void testGetCounsellors_Success() {
        // Arrange
        testEmployee.setRole(Employee.EmployeeRole.COUNSELLOR);
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.COUNSELLOR, Employee.EmployeeStatus.ACTIVE))
            .thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getCounsellors();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(Employee.EmployeeRole.COUNSELLOR, result.get(0).getRole());
        
        verify(employeeRepository).findByRoleAndStatus(Employee.EmployeeRole.COUNSELLOR, Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    void testGetFaculty_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByRoleAndStatus(Employee.EmployeeRole.FACULTY, Employee.EmployeeStatus.ACTIVE))
            .thenReturn(employees);
        
        // Act
        List<EmployeeResponseDTO> result = employeeService.getFaculty();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEmployee.getId(), result.get(0).getId());
        assertEquals(Employee.EmployeeRole.FACULTY, result.get(0).getRole());
        
        verify(employeeRepository).findByRoleAndStatus(Employee.EmployeeRole.FACULTY, Employee.EmployeeStatus.ACTIVE);
    }
    
    @Test
    void testSearchEmployees_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        Page<Employee> employeePage = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("firstName"));
        
        when(employeeRepository.findEmployeesWithFilters(null, null, null, "John", pageable))
            .thenReturn(employeePage);
        
        // Act
        Page<EmployeeResponseDTO> result = employeeService.searchEmployees("John", pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testEmployee.getId(), result.getContent().get(0).getId());
        
        verify(employeeRepository).findEmployeesWithFilters(null, null, null, "John", pageable);
    }
    
    @Test
    void testGetEmployeesByRoleAndDepartment_Success() {
        // Arrange
        List<Employee> employees = Arrays.asList(testEmployee);
        Page<Employee> employeePage = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("firstName"));
        
        when(employeeRepository.findEmployeesWithFilters(Employee.EmployeeStatus.ACTIVE, Employee.EmployeeRole.FACULTY, "IT", null, pageable))
            .thenReturn(employeePage);
        
        // Act
        Page<EmployeeResponseDTO> result = employeeService.getEmployeesByRoleAndDepartment(
            Employee.EmployeeRole.FACULTY, "IT", pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testEmployee.getId(), result.getContent().get(0).getId());
        
        verify(employeeRepository).findEmployeesWithFilters(Employee.EmployeeStatus.ACTIVE, Employee.EmployeeRole.FACULTY, "IT", null, pageable);
    }
    
    @Test
    void testGetEmployeesHiredInDateRange_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<Employee> employees = Arrays.asList(testEmployee);
        Page<Employee> employeePage = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("hireDate"));
        
        when(employeeRepository.findByHireDateBetween(startDate, endDate, pageable))
            .thenReturn(employeePage);
        
        // Act
        Page<EmployeeResponseDTO> result = employeeService.getEmployeesHiredInDateRange(startDate, endDate, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testEmployee.getId(), result.getContent().get(0).getId());
        
        verify(employeeRepository).findByHireDateBetween(startDate, endDate, pageable);
    }
}
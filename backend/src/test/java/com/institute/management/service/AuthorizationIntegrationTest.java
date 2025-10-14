package com.institute.management.service;

import com.institute.management.entity.Employee;
import com.institute.management.entity.Lead;
import com.institute.management.entity.User;
import com.institute.management.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthorizationIntegrationTest {
    
    @Autowired
    private LeadService leadService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private BatchService batchService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private PlacementService placementService;
    
    @Test
    void testAdminCanAccessAllServices() {
        // Set up admin user
        setSecurityContext(Employee.EmployeeRole.ADMIN);
        
        // Test that admin can create a lead
        Lead lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@example.com");
        lead.setPhone("1234567890");
        
        assertDoesNotThrow(() -> {
            leadService.createLead(lead);
        });
    }
    
    @Test
    void testFacultyCannotAccessLeadService() {
        // Set up faculty user
        setSecurityContext(Employee.EmployeeRole.FACULTY);
        
        // Test that faculty cannot create a lead
        Lead lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@example.com");
        lead.setPhone("1234567890");
        
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(lead);
        });
    }
    
    @Test
    void testCounsellorCanAccessLeadService() {
        // Set up counsellor user
        setSecurityContext(Employee.EmployeeRole.COUNSELLOR);
        
        // Test that counsellor can create a lead
        Lead lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@example.com");
        lead.setPhone("1234567890");
        
        assertDoesNotThrow(() -> {
            leadService.createLead(lead);
        });
    }
    
    @Test
    void testPlacementOfficerCannotAccessLeadService() {
        // Set up placement officer user
        setSecurityContext(Employee.EmployeeRole.PLACEMENT_OFFICER);
        
        // Test that placement officer cannot create a lead
        Lead lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@example.com");
        lead.setPhone("1234567890");
        
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(lead);
        });
    }
    
    @Test
    void testOperationsCannotAccessLeadService() {
        // Set up operations user
        setSecurityContext(Employee.EmployeeRole.OPERATIONS);
        
        // Test that operations cannot create a lead
        Lead lead = new Lead();
        lead.setFirstName("John");
        lead.setLastName("Doe");
        lead.setEmail("john.doe@example.com");
        lead.setPhone("1234567890");
        
        assertThrows(AccessDeniedException.class, () -> {
            leadService.createLead(lead);
        });
    }
    
    @Test
    void testOnlyAdminCanCreateEmployee() {
        // Test admin can create employee
        setSecurityContext(Employee.EmployeeRole.ADMIN);
        
        Employee employee = new Employee();
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setEmail("jane.smith@example.com");
        employee.setRole(Employee.EmployeeRole.FACULTY);
        employee.setHireDate(LocalDate.now());
        
        assertDoesNotThrow(() -> {
            employeeService.createEmployee(employee);
        });
        
        // Test counsellor cannot create employee
        setSecurityContext(Employee.EmployeeRole.COUNSELLOR);
        
        Employee employee2 = new Employee();
        employee2.setEmployeeCode("EMP002");
        employee2.setFirstName("Bob");
        employee2.setLastName("Johnson");
        employee2.setEmail("bob.johnson@example.com");
        employee2.setRole(Employee.EmployeeRole.FACULTY);
        employee2.setHireDate(LocalDate.now());
        
        assertThrows(AccessDeniedException.class, () -> {
            employeeService.createEmployee(employee2);
        });
    }
    
    private void setSecurityContext(Employee.EmployeeRole role) {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setEmployeeCode("TEST001");
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setEmail("test@example.com");
        employee.setRole(role);
        employee.setHireDate(LocalDate.now());
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmployee(employee);
        user.setStatus(User.UserStatus.ACTIVE);
        
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            principal, 
            "password", 
            principal.getAuthorities()
        );
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
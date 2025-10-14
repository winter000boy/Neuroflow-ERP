package com.institute.management.security;

import com.institute.management.entity.Employee;
import com.institute.management.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDate;
import java.util.UUID;

public class AuthorizationTestBase {
    
    /**
     * Custom annotation for testing with different user roles
     */
    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
    public @interface WithMockUser {
        String username() default "testuser";
        Employee.EmployeeRole role() default Employee.EmployeeRole.ADMIN;
    }
    
    /**
     * Security context factory for creating mock users with specific roles
     */
    public static class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
        
        @Override
        public SecurityContext createSecurityContext(WithMockUser annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            
            // Create mock employee
            Employee employee = new Employee();
            employee.setId(UUID.randomUUID());
            employee.setEmployeeCode("TEST001");
            employee.setFirstName("Test");
            employee.setLastName("User");
            employee.setEmail("test@example.com");
            employee.setRole(annotation.role());
            employee.setHireDate(LocalDate.now());
            employee.setStatus(Employee.EmployeeStatus.ACTIVE);
            
            // Create mock user
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername(annotation.username());
            user.setPassword("password");
            user.setEmployee(employee);
            user.setStatus(User.UserStatus.ACTIVE);
            
            // Create UserPrincipal
            UserPrincipal principal = UserPrincipal.create(user);
            
            // Create authentication
            Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, 
                "password", 
                principal.getAuthorities()
            );
            
            context.setAuthentication(auth);
            return context;
        }
    }
    
    /**
     * Helper method to create a mock user with specific role
     */
    protected void setSecurityContext(Employee.EmployeeRole role) {
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
        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal, 
            "password", 
            principal.getAuthorities()
        );
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    /**
     * Helper method to clear security context
     */
    protected void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
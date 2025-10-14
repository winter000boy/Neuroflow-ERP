package com.institute.management.security;

import com.institute.management.entity.Employee;
import com.institute.management.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    
    private JwtTokenProvider tokenProvider;
    private Authentication authentication;
    
    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        
        // Set test values using reflection
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "myTestSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(tokenProvider, "accessTokenExpiration", 900000L); // 15 minutes
        ReflectionTestUtils.setField(tokenProvider, "refreshTokenExpiration", 604800000L); // 7 days
        
        // Create test user and employee
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@institute.com");
        employee.setRole(Employee.EmployeeRole.ADMIN);
        employee.setHireDate(LocalDate.now());
        
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("johndoe");
        user.setPassword("encodedPassword");
        user.setEmployee(employee);
        user.setStatus(User.UserStatus.ACTIVE);
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }
    
    @Test
    void testGenerateAccessToken() {
        String token = tokenProvider.generateAccessToken(authentication);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(tokenProvider.validateToken(token));
        assertTrue(tokenProvider.isAccessToken(token));
        assertFalse(tokenProvider.isRefreshToken(token));
    }
    
    @Test
    void testGenerateRefreshToken() {
        String token = tokenProvider.generateRefreshToken(authentication);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(tokenProvider.validateToken(token));
        assertTrue(tokenProvider.isRefreshToken(token));
        assertFalse(tokenProvider.isAccessToken(token));
    }
    
    @Test
    void testGetUsernameFromToken() {
        String token = tokenProvider.generateAccessToken(authentication);
        String username = tokenProvider.getUsernameFromToken(token);
        
        assertEquals("johndoe", username);
    }
    
    @Test
    void testGetUserIdFromToken() {
        String token = tokenProvider.generateAccessToken(authentication);
        String userId = tokenProvider.getUserIdFromToken(token);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        assertEquals(userPrincipal.getId().toString(), userId);
    }
    
    @Test
    void testValidateToken() {
        String token = tokenProvider.generateAccessToken(authentication);
        
        assertTrue(tokenProvider.validateToken(token));
    }
    
    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(tokenProvider.validateToken(invalidToken));
    }
    
    @Test
    void testTokenExpiration() {
        // Create a token provider with very short expiration for testing
        JwtTokenProvider shortExpirationProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(shortExpirationProvider, "jwtSecret", "myTestSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(shortExpirationProvider, "accessTokenExpiration", 1L); // 1 millisecond
        
        String token = shortExpirationProvider.generateAccessToken(authentication);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(shortExpirationProvider.isTokenExpired(token));
        assertFalse(shortExpirationProvider.validateToken(token));
    }
}
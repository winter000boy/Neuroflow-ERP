package com.institute.management.dto;

import com.institute.management.entity.Employee;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private LocalDateTime expiresAt;
    private UserInfoDTO user;
    
    // Constructors
    public AuthResponseDTO() {}
    
    public AuthResponseDTO(String accessToken, String refreshToken, LocalDateTime expiresAt, UserInfoDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public UserInfoDTO getUser() {
        return user;
    }
    
    public void setUser(UserInfoDTO user) {
        this.user = user;
    }
    
    // Nested class for user information
    public static class UserInfoDTO {
        private UUID id;
        private String username;
        private String fullName;
        private String email;
        private Employee.EmployeeRole role;
        private String department;
        
        // Constructors
        public UserInfoDTO() {}
        
        public UserInfoDTO(UUID id, String username, String fullName, String email, 
                          Employee.EmployeeRole role, String department) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.department = department;
        }
        
        // Getters and Setters
        public UUID getId() {
            return id;
        }
        
        public void setId(UUID id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public Employee.EmployeeRole getRole() {
            return role;
        }
        
        public void setRole(Employee.EmployeeRole role) {
            this.role = role;
        }
        
        public String getDepartment() {
            return department;
        }
        
        public void setDepartment(String department) {
            this.department = department;
        }
    }
}
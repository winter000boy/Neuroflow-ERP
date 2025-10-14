package com.institute.management.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Constructors
    public RefreshTokenRequestDTO() {}
    
    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public String toString() {
        return "RefreshTokenRequestDTO{" +
                "refreshToken='[PROTECTED]'" +
                '}';
    }
}
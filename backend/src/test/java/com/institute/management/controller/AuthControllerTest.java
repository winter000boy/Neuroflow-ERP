package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.AuthResponseDTO;
import com.institute.management.dto.LoginRequestDTO;
import com.institute.management.dto.RefreshTokenRequestDTO;
import com.institute.management.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testLoginSuccess() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("johndoe", "password123");
        
        AuthResponseDTO.UserInfoDTO userInfo = new AuthResponseDTO.UserInfoDTO();
        userInfo.setUsername("johndoe");
        
        AuthResponseDTO authResponse = new AuthResponseDTO(
            "access-token", "refresh-token", LocalDateTime.now().plusMinutes(15), userInfo);
        
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(authResponse);
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.username").value("johndoe"));
    }
    
    @Test
    void testLoginInvalidCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("johndoe", "wrongpassword");
        
        when(authService.login(any(LoginRequestDTO.class)))
            .thenThrow(new BadCredentialsException("Invalid username or password"));
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
    
    @Test
    void testLoginAccountLocked() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("johndoe", "password123");
        
        when(authService.login(any(LoginRequestDTO.class)))
            .thenThrow(new LockedException("Account is temporarily locked"));
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.message").value("Account is temporarily locked"));
    }
    
    @Test
    void testLoginValidationError() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("", ""); // Invalid data
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRefreshTokenSuccess() throws Exception {
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO("valid-refresh-token");
        
        AuthResponseDTO.UserInfoDTO userInfo = new AuthResponseDTO.UserInfoDTO();
        userInfo.setUsername("johndoe");
        
        AuthResponseDTO authResponse = new AuthResponseDTO(
            "new-access-token", "new-refresh-token", LocalDateTime.now().plusMinutes(15), userInfo);
        
        when(authService.refreshToken(any(RefreshTokenRequestDTO.class))).thenReturn(authResponse);
        
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }
    
    @Test
    void testRefreshTokenInvalid() throws Exception {
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO("invalid-refresh-token");
        
        when(authService.refreshToken(any(RefreshTokenRequestDTO.class)))
            .thenThrow(new BadCredentialsException("Invalid refresh token"));
        
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));
    }
    
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}
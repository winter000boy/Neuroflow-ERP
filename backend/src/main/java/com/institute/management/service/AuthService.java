package com.institute.management.service;

import com.institute.management.dto.AuthResponseDTO;
import com.institute.management.dto.LoginRequestDTO;
import com.institute.management.dto.RefreshTokenRequestDTO;
import com.institute.management.entity.User;
import com.institute.management.repository.UserRepository;
import com.institute.management.security.JwtTokenProvider;
import com.institute.management.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        // Find user first to handle failed login attempts
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);
        
        try {
            if (user != null) {
                // Check if account is locked
                if (user.isAccountLocked()) {
                    throw new LockedException("Account is temporarily locked due to multiple failed login attempts");
                }
                
                // Check if account is active
                if (user.getStatus() != User.UserStatus.ACTIVE) {
                    throw new DisabledException("Account is not active");
                }
            }
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // If we reach here, authentication was successful
            if (user != null) {
                // Reset failed login attempts and update last login
                user.resetFailedLoginAttempts();
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                
                logger.info("User {} logged in successfully", user.getUsername());
            }
            
            // Generate tokens
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            
            // Get token expiration
            Date expirationDate = tokenProvider.getExpirationDateFromToken(accessToken);
            LocalDateTime expiresAt = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            
            // Create user info DTO
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            AuthResponseDTO.UserInfoDTO userInfo = createUserInfoDTO(user);
            
            return new AuthResponseDTO(accessToken, refreshToken, expiresAt, userInfo);
            
        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            if (user != null) {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);
                
                logger.warn("Failed login attempt for user: {}. Failed attempts: {}", 
                           user.getUsername(), user.getFailedLoginAttempts());
                
                if (user.isAccountLocked()) {
                    throw new LockedException("Account has been locked due to multiple failed login attempts");
                }
            }
            
            throw new BadCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw e;
        }
    }
    
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        
        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        // Check if it's actually a refresh token
        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Token is not a refresh token");
        }
        
        // Get user from refresh token
        String userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        // Check if user is still active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new DisabledException("User account is not active");
        }
        
        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new LockedException("User account is locked");
        }
        
        // Create new authentication object
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities());
        
        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
        
        // Get token expiration
        Date expirationDate = tokenProvider.getExpirationDateFromToken(newAccessToken);
        LocalDateTime expiresAt = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        // Create user info DTO
        AuthResponseDTO.UserInfoDTO userInfo = createUserInfoDTO(user);
        
        logger.info("Token refreshed for user: {}", user.getUsername());
        
        return new AuthResponseDTO(newAccessToken, newRefreshToken, expiresAt, userInfo);
    }
    
    public void logout() {
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // In a more advanced implementation, you might want to:
        // 1. Blacklist the current token
        // 2. Log the logout event
        // 3. Clear any server-side session data
        
        logger.info("User logged out successfully");
    }
    
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token) && tokenProvider.isAccessToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromToken(token);
    }
    
    private AuthResponseDTO.UserInfoDTO createUserInfoDTO(User user) {
        return new AuthResponseDTO.UserInfoDTO(
            user.getId(),
            user.getUsername(),
            user.getEmployee().getFullName(),
            user.getEmployee().getEmail(),
            user.getEmployee().getRole(),
            user.getEmployee().getDepartment()
        );
    }
}
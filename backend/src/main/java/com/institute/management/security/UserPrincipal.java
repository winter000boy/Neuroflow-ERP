package com.institute.management.security;

import com.institute.management.entity.Employee;
import com.institute.management.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {
    
    private final UUID id;
    private final String username;
    private final String password;
    private final Employee.EmployeeRole role;
    private final User.UserStatus status;
    private final boolean accountLocked;
    private final boolean passwordExpired;
    private final Collection<? extends GrantedAuthority> authorities;
    
    public UserPrincipal(UUID id, String username, String password, Employee.EmployeeRole role, 
                        User.UserStatus status, boolean accountLocked, boolean passwordExpired,
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.accountLocked = accountLocked;
        this.passwordExpired = passwordExpired;
        this.authorities = authorities;
    }
    
    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getEmployee().getRole().name())
        );
        
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmployee().getRole(),
            user.getStatus(),
            user.isAccountLocked(),
            user.isPasswordExpired(),
            authorities
        );
    }
    
    public UUID getId() {
        return id;
    }
    
    public Employee.EmployeeRole getRole() {
        return role;
    }
    
    public User.UserStatus getUserStatus() {
        return status;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // We don't implement account expiration
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return !passwordExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return status == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
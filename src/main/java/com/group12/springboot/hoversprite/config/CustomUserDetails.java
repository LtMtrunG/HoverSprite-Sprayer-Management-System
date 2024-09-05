package com.group12.springboot.hoversprite.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Assuming email is the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement your logic
    }

    public Long getId() {
        return id;
    }
}
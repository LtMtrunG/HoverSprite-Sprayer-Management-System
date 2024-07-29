package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

public class InvalidatedTokenService {
    @Autowired
    InvalidatedTokenRepository invalidatedTokenRepository;

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void cleanExpiredTokens(){
        invalidatedTokenRepository.deleteExpiredTokens();
    }
}

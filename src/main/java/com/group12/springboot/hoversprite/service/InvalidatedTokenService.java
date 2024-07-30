package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.entity.InvalidatedToken;
import com.group12.springboot.hoversprite.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;

public class InvalidatedTokenService {
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void cleanExpiredTokens(){
        invalidatedTokenRepository.deleteExpiredTokens();
    }

    public void createInvalidatedToken(String jit, Date expiryTime){
        InvalidatedToken invalidatedToken = new InvalidatedToken();
        invalidatedToken.setId(jit);
        invalidatedToken.setExpiryTime(expiryTime);

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public boolean existsById(String id){
        return invalidatedTokenRepository.existsById(id);
    }
}

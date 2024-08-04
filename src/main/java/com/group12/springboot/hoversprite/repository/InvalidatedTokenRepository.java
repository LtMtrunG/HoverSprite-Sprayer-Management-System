package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidatedToken it WHERE it.expiryTime < CURRENT_TIMESTAMP")
    int deleteExpiredTokens();
}

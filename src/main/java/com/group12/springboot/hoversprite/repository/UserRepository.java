package com.group12.springboot.hoversprite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group12.springboot.hoversprite.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

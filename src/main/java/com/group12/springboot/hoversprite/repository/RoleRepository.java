package com.group12.springboot.hoversprite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group12.springboot.hoversprite.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
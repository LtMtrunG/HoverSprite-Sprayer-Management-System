package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
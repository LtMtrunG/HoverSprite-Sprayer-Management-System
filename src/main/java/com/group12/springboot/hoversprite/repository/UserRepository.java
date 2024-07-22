package com.group12.springboot.hoversprite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group12.springboot.hoversprite.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}

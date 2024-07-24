package com.group12.springboot.hoversprite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group12.springboot.hoversprite.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

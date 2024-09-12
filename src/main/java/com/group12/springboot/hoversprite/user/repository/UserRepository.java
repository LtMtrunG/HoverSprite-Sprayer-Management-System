package com.group12.springboot.hoversprite.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group12.springboot.hoversprite.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role.name = 'FARMER'")
    Optional<User> findFarmerById(Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role.name = 'RECEPTIONIST'")
    Optional<User> findReceptionistById(Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role.name = 'SPRAYER'")
    Optional<User> findSprayerById(Long id);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.name = 'SPRAYER' AND u.id NOT IN :excludedIds")
    Page<User> findPagedSprayersExcludeByIds(
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.name = 'SPRAYER' AND u.id NOT IN :excludedIds")
    List<User> findAllSprayersExcludeByIds(
            @Param("excludedIds") List<Long> excludedIds);
}

package com.group12.springboot.hoversprite.field.repository;

import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findFieldById(Long id);

    @Query("SELECT f FROM Field f WHERE f.id IN :fieldIds")
    Page<Field> findFieldsOfFarmer(@Param("fieldIds") List<Long> fieldIds, Pageable pageable);

    @Query("SELECT f FROM Field f WHERE f.id IN :fieldIds")
    List<Field> findFieldsOfFarmer(@Param("fieldIds") List<Long> fieldIds);

    Page<Field> findByIdIn(List<Long> fieldIds, Pageable pageable);

    @Query("SELECT f.longitude, f.latitude FROM Field f WHERE f.id IN :fieldIds")
    List<Object[]> findFieldCoordinatesByIds(@Param("fieldIds") List<Long> fieldIds);
}

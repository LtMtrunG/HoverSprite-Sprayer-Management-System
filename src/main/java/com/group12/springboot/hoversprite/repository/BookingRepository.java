package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.Booking;
import com.group12.springboot.hoversprite.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
//    @Query("SELECT b FROM Booking b WHERE b.user.email = :email")
//    Page<Booking> findByUserEmail(@Param("email") String email, Pageable pageable);
    @Query("SELECT b FROM Booking b ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findAllOrderByStatus(Pageable pageable);
    @Query("SELECT b FROM Booking b WHERE b.user.email = :email ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findByUserEmailOrderByStatus(@Param("email") String email, Pageable pageable);
}

package com.group12.springboot.hoversprite.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group12.springboot.hoversprite.booking.entity.Booking;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    // List<Booking> findByUser(User user);
    Optional<Booking> findById(Long id);
    Optional<Booking> findByFeedbackId(Long feedbackId);
    List<Booking> findByTimeSlotId(Long timeSlotId);
    boolean existsById(Long id);
    //    @Query("SELECT b FROM Booking b WHERE b.user.email = :email")
//    Page<Booking> findByUserEmail(@Param("email") String email, Pageable pageable);
    @Query("SELECT b FROM Booking b ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findAllOrderByStatus(Pageable pageable);
    @Query("SELECT b FROM Booking b WHERE b.farmerId = :farmerId ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findByFarmerIdOrderByStatus(@Param("farmerId") Long farmerId, Pageable pageable);
}
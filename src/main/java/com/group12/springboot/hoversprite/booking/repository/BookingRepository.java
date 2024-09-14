package com.group12.springboot.hoversprite.booking.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
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
    List<Booking> findByFieldId(Long fieldId);
    List<Booking> findByFieldIdAndStatusNot(Long fieldId, BookingStatus status);
    Page<Booking> findByFarmerIdAndStatus(Long farmerId, BookingStatus status, Pageable pageable);
    boolean existsById(Long id);
    @Query("SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.createdTime DESC")
    List<Booking> findByStatus(@Param("status") BookingStatus status);
    //    @Query("SELECT b FROM Booking b WHERE b.user.email = :email")
//    Page<Booking> findByUserEmail(@Param("email") String email, Pageable pageable);
    @Query("SELECT b FROM Booking b ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findAllOrderByStatus(Pageable pageable);
    @Query("SELECT b FROM Booking b WHERE b.farmerId = :farmerId ORDER BY CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END, b.createdTime DESC")
    Page<Booking> findByFarmerIdOrderByStatus(@Param("farmerId") Long farmerId, Pageable pageable);
    Page<Booking> findByFarmerIdAndStatusIn(Long farmerId, List<BookingStatus> bookingStatuses, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN Field f ON b.fieldId = f.id " +
            "WHERE b.farmerId = :farmerId " +
            "AND CAST(f.cropType AS string) LIKE %:keyword% " +
            "AND b.status IN :statuses")
    Page<Booking> findByFarmerIdAndFieldCropTypeContainingKeywordAndStatusIn(
            @Param("farmerId") Long farmerId,
            @Param("keyword") String keyword,
            @Param("statuses") List<BookingStatus> statuses,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b JOIN Field f ON b.fieldId = f.id " +
            "WHERE b.farmerId = :farmerId " +
            "AND CAST(f.cropType AS string) LIKE %:keyword% ")
    Page<Booking> findByFarmerIdAndFieldCropTypeContainingKeyword(
            @Param("farmerId") Long farmerId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b JOIN Field f ON b.fieldId = f.id " +
            "WHERE b.farmerId = :farmerId " +
            "AND CAST(f.cropType AS string) LIKE %:keyword% " +
            "AND b.status = :status")
    Page<Booking> findByFarmerIdAndFieldCropTypeContainingKeywordAndStatus(
            @Param("farmerId") Long farmerId,
            @Param("keyword") String keyword,
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.farmerId = :farmerId AND CAST(b.id AS string) LIKE %:id%")
    Page<Booking> findByFarmerIdAndIdContaining(
            @Param("farmerId") Long farmerId,
            @Param("id") String id,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.farmerId = :farmerId AND CAST(b.id AS string) LIKE %:keyword% AND b.status IN :statuses")
    Page<Booking> findByFarmerIdAndIdContainingAndStatusIn(
            @Param("farmerId") Long farmerId,
            @Param("keyword") String keyword,
            @Param("statuses") List<BookingStatus> statuses,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.farmerId = :farmerId AND CAST(b.id AS string) LIKE %:keyword% AND b.status = :status")
    Page<Booking> findByFarmerIdAndIdContainingAndStatus(
            @Param("farmerId") Long farmerId,
            @Param("keyword") String keyword,
            @Param("status") BookingStatus status,
            Pageable pageable
    );
}
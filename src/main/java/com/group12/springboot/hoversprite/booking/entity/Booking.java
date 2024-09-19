package com.group12.springboot.hoversprite.booking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.group12.springboot.hoversprite.booking.enums.BookingStatus;

import com.group12.springboot.hoversprite.booking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TBL_BOOKINGS")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="receptionist_id")
    private Long receptionistId;

    @Column(name="farmer_id")
    private Long farmerId;

    @ElementCollection
    @CollectionTable(name = "booking_sprayers", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "sprayers_id")
    private List<Long> sprayersId;

    @Column(name="in_progress_sprayers_id")
    private List<Long> inProgressSprayerIds;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private BookingStatus status;

    @Column(name="created_time")
    private LocalDateTime createdTime;

    @Column(name="time_slot_id")
    private Long timeSlotId;

    @Column(name="field_id")
    private Long fieldId;

    @Column(name="total_cost")
    private double totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_status")
    private PaymentStatus paymentStatus;

    @Column(name="feedback_id")
    private Long feedbackId;

    public Booking(){};

    public Booking(Booking booking) {
        this.receptionistId = booking.receptionistId;
        this.farmerId = booking.farmerId;
        this.sprayersId = new ArrayList<>(booking.sprayersId);
        this.inProgressSprayerIds = new ArrayList<>(booking.inProgressSprayerIds);
        this.status = booking.status;
        this.createdTime = booking.createdTime;
        this.timeSlotId = booking.timeSlotId;
        this.fieldId = booking.fieldId;
        this.totalCost = booking.totalCost;
        this.paymentStatus = booking.paymentStatus;
        this.feedbackId = booking.feedbackId;
    }
}
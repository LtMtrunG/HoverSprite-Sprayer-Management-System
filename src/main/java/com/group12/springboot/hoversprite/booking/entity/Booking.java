package com.group12.springboot.hoversprite.booking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.enums.CropType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name="sprayers_id")
    private List<Long> sprayersId;

    @Column(name="in_progress_sprayers_id")
    private List<Long> inProgressSprayerIds;

    @Enumerated(EnumType.STRING)
    @Column(name="crop_type")
    private CropType cropType;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private BookingStatus status;

    @Column(name="farm_land_area")
    private double farmlandArea;

    @Column(name="created_time")
    private LocalDateTime createdTime;

    @Column(name="time_slot_id")
    private Long timeSlotId;

    @Column(name="total_cost")
    private double totalCost;

    public Booking(){};

    public Booking(Booking booking) {
        this.receptionistId = booking.receptionistId;
        this.farmerId = booking.farmerId;
        this.sprayersId = new ArrayList<>(booking.sprayersId);
        this.inProgressSprayerIds = new ArrayList<>(booking.inProgressSprayerIds);
        this.cropType = booking.cropType;
        this.status = booking.status;
        this.farmlandArea = booking.farmlandArea;
        this.createdTime = booking.createdTime;
        this.timeSlotId = booking.timeSlotId;
        this.totalCost = booking.totalCost;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getReceptionistId() {
//        return receptionistId;
//    }
//
//    public void setReceptionistId(Long receptionistId) {
//        this.receptionistId = receptionistId;
//    }
//
//    public Long getFarmerId() {
//        return farmerId;
//    }
//
//    public void setFarmerId(Long farmerId) {
//        this.farmerId = farmerId;
//    }
//
//    public List<Long> getSprayersId() {
//        return sprayersId;
//    }
//
//    public void setSprayersId(List<Long> sprayersId) {
//        this.sprayersId = sprayersId;
//    }
//
//    public CropType getCropType() {
//        return cropType;
//    }
//
//    public void setCropType(CropType cropType) {
//        this.cropType = cropType;
//    }
//
//    public BookingStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(BookingStatus status) {
//        this.status = status;
//    }
//
//    public double getFarmlandArea() {
//        return farmlandArea;
//    }
//
//    public void setFarmlandArea(double farmlandArea) {
//        this.farmlandArea = farmlandArea;
//    }
//
//    public LocalDateTime getCreatedTime() {
//        return createdTime;
//    }
//
//    public void setCreatedTime(LocalDateTime createdTime) {
//        this.createdTime = createdTime;
//    }
//
//    public Long getTimeSlotId() {
//        return timeSlotId;
//    }
//
//    public void setTimeSlotId(Long timeSlotId) {
//        this.timeSlotId = timeSlotId;
//    }
//
//    public double getTotalCost() {
//        return totalCost;
//    }
//
//    public void setTotalCost(double totalCost) {
//        this.totalCost = totalCost;
//    }
}
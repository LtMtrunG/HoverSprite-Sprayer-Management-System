package com.group12.springboot.hoversprite.booking;

import java.time.LocalDateTime;
import java.util.List;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.enums.CropType;

public class BookingResponse {
    private Long id;
    private Long receptionistId;
    private Long farmerId;
    private List<Long> sprayersId;
    private CropType cropType;
    private BookingStatus status;
    private double farmlandArea;
    private LocalDateTime createdTime;
    private Long timeSlotId;
    private double totalCost;

    public BookingResponse(Booking booking){
        this.id = booking.getId();
        this.receptionistId = booking.getReceptionistId();
        this.farmerId = booking.getFarmerId();
        this.sprayersId = booking.getSprayersId();
        this.cropType = booking.getCropType();
        this.status = booking.getStatus();
        this.farmlandArea = booking.getFarmlandArea();
        this.createdTime = booking.getCreatedTime();
        this.timeSlotId = booking.getTimeSlotId();
        this.totalCost = booking.getTotalCost();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceptionistId() {
        return receptionistId;
    }

    public void setReceptionistId(Long receptionistId) {
        this.receptionistId = receptionistId;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmer(Long farmerId) {
        this.farmerId = farmerId;
    }

    public List<Long> getSprayersId() {
        return sprayersId;
    }

    public void setSprayers(List<Long> sprayersId) {
        this.sprayersId = sprayersId;
    }

    public CropType getCropType() {
        return cropType;
    }

    public void setCropType(CropType cropType) {
        this.cropType = cropType;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public double getFarmlandArea() {
        return farmlandArea;
    }

    public void setFarmlandArea(double farmlandArea) {
        this.farmlandArea = farmlandArea;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlot(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
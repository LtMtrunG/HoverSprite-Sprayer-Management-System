package com.group12.springboot.hoversprite.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.group12.springboot.hoversprite.booking.enums.CropType;
import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;

public class BookingCreationRequest {
    private Long receptionistId;
    private Long farmerId;
    private CropType cropType;
    private double farmlandArea;
    private LocalDateTime createdTime;
    @DateConstraint
    private LocalDate date;
    @StartTimeConstraint
    private LocalTime startTime;

    public CropType getCropType() {
        return cropType;
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

    public void setCropType(CropType cropType) {
        this.cropType = cropType;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
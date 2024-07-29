package com.group12.springboot.hoversprite.dataTransferObject.request.booking;

import com.group12.springboot.hoversprite.constraint.StartTimeConstraint;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.entity.enums.CropType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BookingCreationRequest {
    private User receptionist;
    private User user;
    private List<User> sprayers;
    private CropType cropType;
    private double farmlandArea;
    private LocalDateTime createdTime;
    private LocalDate date;
    @StartTimeConstraint
    private LocalTime startTime;


    public User getReceptionist() {
        return receptionist;
    }

    public void setReceptionist(User receptionist) {
        this.receptionist = receptionist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getSprayers() {
        return sprayers;
    }

    public void setSprayers(List<User> sprayers) {
        this.sprayers = sprayers;
    }

    public CropType getCropType() {
        return cropType;
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

package com.group12.springboot.hoversprite.dataTransferObject.request;

import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.entity.enums.CropType;

import java.time.LocalDateTime;
import java.util.List;

public class BookingCreationRequest {
    private User receptionist;
    private User user;
    private List<User> sprayers;
    private CropType cropType;
    private BookingStatus status;
    private double farmlandArea;
    private LocalDateTime createdTime;
    private LocalDateTime sprayingTime;
    private double totalCost;


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

    public LocalDateTime getSprayingTime() {
        return sprayingTime;
    }

    public void setSprayingTime(LocalDateTime sprayingTime) {
        this.sprayingTime = sprayingTime;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}

package com.group12.springboot.hoversprite.dataTransferObject.response.booking;

import java.time.LocalDateTime;
import java.util.List;

import com.group12.springboot.hoversprite.entity.Booking;
import com.group12.springboot.hoversprite.entity.TimeSlot;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.entity.enums.CropType;

public class BookingResponse {
    private Long id;
    private User receptionist;
    private User user;
    private List<User> sprayers;
    private CropType cropType;
    private BookingStatus status;
    private double farmlandArea;
    private LocalDateTime createdTime;
    private TimeSlot timeSlot;
    private double totalCost;

    public BookingResponse(Booking booking){
        this.id = booking.getId();
        this.receptionist = booking.getReceptionist();
        this.user = booking.getUser();
        this.sprayers = booking.getSprayers();
        this.cropType = booking.getCropType();
        this.status = booking.getStatus();
        this.farmlandArea = booking.getFarmlandArea();
        this.createdTime = booking.getCreatedTime();
        this.timeSlot = booking.getTimeSlot();
        this.totalCost = booking.getTotalCost();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
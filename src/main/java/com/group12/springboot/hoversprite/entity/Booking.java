package com.group12.springboot.hoversprite.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.group12.springboot.hoversprite.entity.enums.BookingStatus;
import com.group12.springboot.hoversprite.entity.enums.CropType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TBL_BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="receptionist_id")
    private User receptionist;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "TBL_BOOKINGS_SPRAYERS",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "sprayer_id")
    )
    private List<User> sprayers;

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

    @ManyToOne
    @JoinColumn(name="time_slot_id")
    private TimeSlot timeSlot;

    @Column(name="total_cost")
    private double totalCost;

    public Booking(){};

    public Booking(Booking booking) {
        this.receptionist = booking.receptionist;
        this.user = booking.user;
        this.sprayers = new ArrayList<>(booking.sprayers);
        this.cropType = booking.cropType;
        this.status = booking.status;
        this.farmlandArea = booking.farmlandArea;
        this.createdTime = booking.createdTime;
        this.timeSlot = booking.timeSlot;
        this.totalCost = booking.totalCost;
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
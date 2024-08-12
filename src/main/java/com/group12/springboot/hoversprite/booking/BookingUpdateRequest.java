package com.group12.springboot.hoversprite.booking;

import java.util.List;

import com.group12.springboot.hoversprite.booking.enums.CropType;

public class BookingUpdateRequest {
    private Long id;
    private List<Long> sprayersId;
    private CropType cropType;
    private double farmlandArea;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public List<Long> getSprayersId() {
        return sprayersId;
    }

    public void setSprayersId(List<Long> sprayersId) {
        this.sprayersId = sprayersId;
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
}
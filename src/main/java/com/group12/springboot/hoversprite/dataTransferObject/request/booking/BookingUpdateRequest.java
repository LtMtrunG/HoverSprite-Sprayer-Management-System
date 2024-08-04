package com.group12.springboot.hoversprite.dataTransferObject.request.booking;

import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.entity.enums.CropType;

import java.util.List;

public class BookingUpdateRequest {
    private Long id;
    private List<User> sprayers;
    private CropType cropType;
    private double farmlandArea;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

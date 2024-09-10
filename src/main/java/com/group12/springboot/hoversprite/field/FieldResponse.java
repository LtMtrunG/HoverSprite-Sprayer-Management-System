package com.group12.springboot.hoversprite.field;

import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.field.enums.CropType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FieldResponse {
    private Long id;
    private String name;
    private double longitude;
    private double latitude;
    private String address;
    private CropType cropType;
    private double farmlandArea;
    private LocalDate lastSprayingDate;

    public FieldResponse(Field field) {
        this.id = field.getId();
        this.name = field.getName();
        this.longitude = field.getLongitude();
        this.latitude = field.getLatitude();
        this.address = field.getAddress();
        this.cropType = field.getCropType();
        this.farmlandArea = field.getFarmlandArea();
        this.lastSprayingDate = field.getLastSprayingDate();
    }
}

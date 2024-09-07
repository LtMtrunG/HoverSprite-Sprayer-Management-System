package com.group12.springboot.hoversprite.field;

import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.field.enums.CropType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldResponse {
    private Long id;
    private String name;
    private float longitude;
    private float latitude;
    private String address;
    private CropType cropType;
    private double farmlandArea;

    public FieldResponse(Field field) {
        this.id = field.getId();
        this.longitude = field.getLongitude();
        this.latitude = field.getLatitude();
        this.address = field.getAddress();
        this.cropType = field.getCropType();
    }
}

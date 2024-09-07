package com.group12.springboot.hoversprite.field;

import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.field.enums.CropType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FieldDTO {
    private final Long id;
    private final String name;
    private final float longitude;
    private final float latitude;
    private final String address;
    private final CropType cropType;
    private final double farmlandArea;

    public FieldDTO(Field field) {
        this.id = field.getId();
        this.name = field.getName();
        this.longitude = field.getLongitude();
        this.latitude = field.getLatitude();
        this.address = field.getAddress();
        this.cropType = field.getCropType();
        this.farmlandArea = field.getFarmlandArea();
    }
}

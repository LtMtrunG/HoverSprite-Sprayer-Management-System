package com.group12.springboot.hoversprite.field;

import com.group12.springboot.hoversprite.field.enums.CropType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FieldUpdateRequest {
    private Long fieldId;
    private String name;
    private double longitude;
    private double latitude;
    private String address;
    private CropType cropType;
    private double farmlandArea;
}

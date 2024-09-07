package com.group12.springboot.hoversprite.field;

import com.group12.springboot.hoversprite.field.enums.CropType;
import com.group12.springboot.hoversprite.validator.EmailConstraint;
import com.group12.springboot.hoversprite.validator.NameConstraint;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FieldCreationRequest {
    private Long farmerId;
    private String name;
    private float longitude;
    private float latitude;
    private String address;
    private CropType cropType;
    private double farmlandArea;
}

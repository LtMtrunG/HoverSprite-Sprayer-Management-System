package com.group12.springboot.hoversprite.field.entity;

import com.group12.springboot.hoversprite.field.enums.CropType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="TBL_FIELDS")
@NoArgsConstructor
@Getter
@Setter
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="longitude")
    private double longitude;

    @Column(name="latitude")
    private double latitude;

    @Column(name="address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name="crop_type")
    private CropType cropType;

    @Column(name="farm_land_area")
    private double farmlandArea;

    @Column(name="last_spraying_date")
    private LocalDate lastSprayingDate;
}

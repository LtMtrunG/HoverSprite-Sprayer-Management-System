package com.group12.springboot.hoversprite.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.group12.springboot.hoversprite.booking.enums.CropType;
import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingCreationRequest {
    private Long receptionistId;
    private Long farmerId;
    private CropType cropType;
    private double farmlandArea;
    private LocalDateTime createdTime;
    @DateConstraint
    private LocalDate date;
    @StartTimeConstraint
    private LocalTime startTime;
}
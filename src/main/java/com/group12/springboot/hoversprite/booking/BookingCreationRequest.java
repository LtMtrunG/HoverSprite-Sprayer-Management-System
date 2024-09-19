package com.group12.springboot.hoversprite.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingCreationRequest {
    private Long farmerId;
    private Long fieldId;
    private LocalDateTime createdTime;
    @DateConstraint
    private LocalDate date;
    @StartTimeConstraint
    private LocalTime startTime;
}
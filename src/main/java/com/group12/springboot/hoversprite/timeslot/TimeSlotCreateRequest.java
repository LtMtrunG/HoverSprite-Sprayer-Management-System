package com.group12.springboot.hoversprite.timeslot;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TimeSlotCreateRequest {
    private LocalDate date;
    private LocalTime startTime;
}
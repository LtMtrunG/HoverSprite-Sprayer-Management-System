package com.group12.springboot.hoversprite.timeslot;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TimeSlotByDateRequest {
    private LocalDate date;
}
package com.group12.springboot.hoversprite.timeslot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;
import lombok.Getter;

@Getter
public class TimeSlotBookingDTO {
    private final Long id;

    @DateConstraint
    private final LocalDate date;

    private final DayOfWeek dayOfWeek;

    @StartTimeConstraint
    private final LocalTime startTime;

    private final LocalTime endTime;

    public TimeSlotBookingDTO(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.date = timeSlot.getDate();
        this.dayOfWeek = timeSlot.getDayOfWeek();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
    }
}

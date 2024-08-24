package com.group12.springboot.hoversprite.timeslot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeSlotCreateResponse {
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;

    private LocalTime endTime;

    private int maxSessions = 2;

    private int bookedSessions;

    public TimeSlotCreateResponse(TimeSlot timeSlot){
        this.date = timeSlot.getDate();
        this.dayOfWeek = timeSlot.getDayOfWeek();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
        this.bookedSessions = timeSlot.getBookedSessions();
    }
}
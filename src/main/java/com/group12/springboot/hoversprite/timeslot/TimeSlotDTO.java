package com.group12.springboot.hoversprite.timeslot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;

public class TimeSlotDTO {
    private final Long id;

    @DateConstraint
    private final LocalDate date;

    private final DayOfWeek dayOfWeek;

    @StartTimeConstraint
    private final LocalTime startTime;

    private final LocalTime endTime;

    private final int maxSessions;

    private final int bookedSessions;

    public TimeSlotDTO(TimeSlot timeSlot) {
        this.id = timeSlot.getId();
        this.date = timeSlot.getDate();
        this.dayOfWeek = timeSlot.getDayOfWeek();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
        this.maxSessions = timeSlot.getMaxSessions();
        this.bookedSessions = timeSlot.getBookedSessions();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public int getBookedSessions() {
        return bookedSessions;
    }  
}

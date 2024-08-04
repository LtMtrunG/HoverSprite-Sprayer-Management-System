package com.group12.springboot.hoversprite.dataTransferObject.request.timeslot;

import com.group12.springboot.hoversprite.constraint.DateConstraint;
import com.group12.springboot.hoversprite.constraint.StartTimeConstraint;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotCreateRequest {
    @DateConstraint
    private LocalDate date;
    @StartTimeConstraint
    private LocalTime startTime;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}

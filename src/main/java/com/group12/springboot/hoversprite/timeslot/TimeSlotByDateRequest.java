package com.group12.springboot.hoversprite.timeslot;

import java.time.LocalDate;

public class TimeSlotByDateRequest {
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
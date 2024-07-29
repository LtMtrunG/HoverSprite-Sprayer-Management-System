package com.group12.springboot.hoversprite.dataTransferObject.request.timeslot;

import java.time.LocalDate;

public class TimeSlotByDateRequest {
    private LocalDate date;

    public TimeSlotByDateRequest() {}

    public TimeSlotByDateRequest(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
package com.group12.springboot.hoversprite.dataTransferObject.response;

import com.group12.springboot.hoversprite.entity.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public int getBookedSessions() {
        return bookedSessions;
    }

    public void setBookedSessions(int bookedSessions) {
        this.bookedSessions = bookedSessions;
    }
}
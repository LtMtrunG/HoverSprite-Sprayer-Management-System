package com.group12.springboot.hoversprite.dataTransferObject.response;

import com.group12.springboot.hoversprite.entity.TimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeSlotByDateResponse {
    private int size;
    private int totalSessions;
    private int bookedSessions;
    private boolean isFull = false;
    private List<TimeSlot> timeSlots;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getBookedSessions() {
        return bookedSessions;
    }

    public void setBookedSessions(int bookedSessions) {
        this.bookedSessions = bookedSessions;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}

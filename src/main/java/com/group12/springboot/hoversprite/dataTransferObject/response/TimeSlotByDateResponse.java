package com.group12.springboot.hoversprite.dataTransferObject.response;

import com.group12.springboot.hoversprite.entity.TimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeSlotByDateResponse {
    private int size;
    private Set<TimeSlot> timeSlots;
    private boolean isFull = false;

    public TimeSlotByDateResponse(List<TimeSlot> timeSlots) {
        this.size = timeSlots.size();
        this.timeSlots = new HashSet<>(timeSlots);
        if (this.size == 6) {
            this.isFull = true;
        }
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(Set<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}

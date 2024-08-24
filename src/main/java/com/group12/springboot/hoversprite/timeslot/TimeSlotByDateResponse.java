package com.group12.springboot.hoversprite.timeslot;

import java.util.List;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeSlotByDateResponse {
    private int size;
    private int totalSessions;
    private int bookedSessions;
    private boolean isFull = false;
    private List<TimeSlot> timeSlots;
}
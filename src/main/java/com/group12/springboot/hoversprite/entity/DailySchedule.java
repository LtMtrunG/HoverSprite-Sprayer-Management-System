package com.group12.springboot.hoversprite.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DailySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private List<TimeSlot> timeSlots = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void initializeTimeSlots() {
        LocalTime[] morningSlots = {
                LocalTime.of(4, 0), LocalTime.of(5, 0), LocalTime.of(6, 0), LocalTime.of(7, 0)
        };
        LocalTime[] afternoonSlots = {
                LocalTime.of(16, 0), LocalTime.of(17, 0)
        };

        for (LocalTime time : morningSlots) {
            timeSlots.add(new TimeSlot(time, time.plusHours(1)));
        }
        for (LocalTime time : afternoonSlots) {
            timeSlots.add(new TimeSlot(time, time.plusHours(1)));
        }
    }

    public List<TimeSlot> getAvailableTimeSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (slot.isAvailable()) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }
}

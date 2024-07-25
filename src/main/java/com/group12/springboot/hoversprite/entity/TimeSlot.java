package com.group12.springboot.hoversprite.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="TBL_TIME_SLOTS")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "max_sessions")
    private int maxSessions = 2;

    @Column(name = "booked_sessions")
    private int bookedSessions = 0;

    public TimeSlot() {}

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getBookedSessions() {
        return bookedSessions;
    }

    public void setBookedSessions(int bookedSessions) {
        this.bookedSessions = bookedSessions;
    }

    public boolean isAvailable() {
        return bookedSessions < maxSessions;
    }

    public void bookSession() {
        if (isAvailable()) {
            bookedSessions++;
        } else {
            throw new RuntimeException("TimeSlot fully booked");
        }
    }

    public void cancelSession() {
        if (bookedSessions > 0) {
            bookedSessions--;
        }
    }
}

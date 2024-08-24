package com.group12.springboot.hoversprite.timeslot.entity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="TBL_TIME_SLOTS")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    @DateConstraint
    private LocalDate date;

    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    @StartTimeConstraint
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "max_sessions")
    private int maxSessions = 2;

    @Column(name = "booked_sessions")
    private int bookedSessions = 0;

    @Column(name = "booked_sprayers_id")
    private List<Long> bookedSprayersId;

    public TimeSlot() {}

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
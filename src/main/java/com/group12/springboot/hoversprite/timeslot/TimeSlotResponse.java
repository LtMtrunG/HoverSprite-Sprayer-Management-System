package com.group12.springboot.hoversprite.timeslot;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import com.group12.springboot.hoversprite.validator.DateConstraint;
import com.group12.springboot.hoversprite.validator.StartTimeConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
public class TimeSlotResponse {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlotResponse(TimeSlot timeSlot) {
        this.date = timeSlot.getDate();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
    }
}

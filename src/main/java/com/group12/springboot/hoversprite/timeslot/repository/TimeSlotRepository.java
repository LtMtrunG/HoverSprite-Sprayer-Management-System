package com.group12.springboot.hoversprite.timeslot.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findAllByDate(LocalDate date);
    Optional<TimeSlot> findByDateAndStartTime(LocalDate date, LocalTime startTime);
    List<TimeSlot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<TimeSlot> findById(Long id);
}
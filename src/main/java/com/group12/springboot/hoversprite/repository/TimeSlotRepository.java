package com.group12.springboot.hoversprite.repository;

import com.group12.springboot.hoversprite.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findAllByDate(LocalDate date);
    Optional<TimeSlot> findByDateAndStartTime(LocalDate date, LocalTime startTime);
    List<TimeSlot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}

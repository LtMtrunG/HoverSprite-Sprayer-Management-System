package com.group12.springboot.hoversprite.timeslot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface TimeSlotAPI {
    public TimeSlotCreateResponse createTimeSlot(TimeSlotCreateRequest request);

    public TimeSlotByDateResponse getTimeSlotByDate(TimeSlotByDateRequest request);

    public List<TimeSlotByDateResponse> getTimeSlotByWeek(TimeSlotByDateRequest request);

    public List<TimeSlotDTO> getTimeSlotByWeek(LocalDate bookingDate);

    public Optional<TimeSlotDTO> findByDateAndStartTime(LocalDate date, LocalTime startTime);

    public TimeSlotDTO findById(Long id);

    public void saveTimeSlot(TimeSlotDTO timeSlotDTO);

    public boolean isAvailable(TimeSlotDTO timeSlotDTO);

    public void bookSession(TimeSlotDTO timeSlotDTO);

    public void cancelSession(Long timeSlotId);

    public void setBookedSprayersId(Long timeSlotId, List<Long> bookedSprayersId);
}
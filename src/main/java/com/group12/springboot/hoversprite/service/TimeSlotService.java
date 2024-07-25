package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.entity.TimeSlot;
import com.group12.springboot.hoversprite.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimeSlotService {
    @Autowired
    TimeSlotRepository timeSlotRepository;

    @PreAuthorize("hasAuthority('APPROVE_BOOKING')")
    public TimeSlotCreateResponse createTimeSlot(TimeSlotCreateRequest request) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findByDateAndStartTime(request.getDate(), request.getStartTime());

        if (optionalTimeSlot.isPresent()) {
            throw new IllegalArgumentException("TimeSlot already exists for the given date and start time.");
        }

        TimeSlot newTimeSlot = new TimeSlot(request.getDate(), request.getStartTime(),  request.getStartTime().plusHours(1));
        newTimeSlot = timeSlotRepository.save(newTimeSlot);

        return new TimeSlotCreateResponse(newTimeSlot);
    }

    @PreAuthorize("hasAuthority('APPROVE_BOOKING')")
    public TimeSlotByDateResponse getTimeSlotByDate(TimeSlotByDateRequest request) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAllByDate(request.getDate());
        return new TimeSlotByDateResponse(timeSlots);
    }
}

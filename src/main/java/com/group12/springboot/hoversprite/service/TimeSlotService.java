package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.entity.TimeSlot;
import com.group12.springboot.hoversprite.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeSlotService {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private static final Set<LocalTime> VALID_TIMES = new HashSet<>();

    static {
        VALID_TIMES.add(LocalTime.of(4, 0));
        VALID_TIMES.add(LocalTime.of(5, 0));
        VALID_TIMES.add(LocalTime.of(6, 0));
        VALID_TIMES.add(LocalTime.of(7, 0));
        VALID_TIMES.add(LocalTime.of(16, 0));
        VALID_TIMES.add(LocalTime.of(17, 0));
    }

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

        Set<LocalTime> existingStartTimes = timeSlots.stream()
                .map(TimeSlot::getStartTime)
                .collect(Collectors.toSet());

        Set<LocalTime> missingStartTimes = VALID_TIMES.stream()
                .filter(startTime -> !existingStartTimes.contains(startTime))
                .collect(Collectors.toSet());

        for (LocalTime startTime : missingStartTimes) {
            TimeSlot newTimeSlot = new TimeSlot(request.getDate(), startTime, startTime.plusHours(1));
            timeSlotRepository.save(newTimeSlot);
            timeSlots.add(newTimeSlot);
        }
        TimeSlotByDateResponse timeSlotByDateResponse = new TimeSlotByDateResponse();
        timeSlotByDateResponse.setSize(timeSlots.size());
        timeSlotByDateResponse.setTimeSlots(timeSlots);
        timeSlotByDateResponse.setTotalSessions(2 * timeSlotByDateResponse.getSize());
        timeSlotByDateResponse.setBookedSessions(timeSlots.stream()
                                                        .mapToInt(TimeSlot::getBookedSessions)
                                                        .sum());
        boolean isFull = (timeSlotByDateResponse.getBookedSessions() == timeSlotByDateResponse.getTotalSessions());
        timeSlotByDateResponse.setFull(isFull);

        return timeSlotByDateResponse;
    }

    @PreAuthorize("hasAuthority('APPROVE_BOOKING')")
    public List<TimeSlotByDateResponse> getTimeSlotByWeek(TimeSlotByDateRequest request) {
        LocalDate startDate = request.getDate().with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        List<TimeSlot> allTimeSlots = timeSlotRepository.findAllByDateBetween(startDate, endDate);

        Map<LocalDate, List<TimeSlot>> timeSlotsByDate = allTimeSlots.stream()
                .collect(Collectors.groupingBy(TimeSlot::getDate));

        List<TimeSlotByDateResponse> responses = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<TimeSlot> timeSlotsForDate = timeSlotsByDate.getOrDefault(date, new ArrayList<>());

            Set<LocalTime> existingStartTimes = timeSlotsForDate.stream()
                    .map(TimeSlot::getStartTime)
                    .collect(Collectors.toSet());

            Set<LocalTime> missingStartTimes = VALID_TIMES.stream()
                    .filter(startTime -> !existingStartTimes.contains(startTime))
                    .collect(Collectors.toSet());

            for (LocalTime startTime : missingStartTimes) {
                TimeSlot newTimeSlot = new TimeSlot(date, startTime, startTime.plusHours(1));
                timeSlotRepository.save(newTimeSlot);
                timeSlotsForDate.add(newTimeSlot);
            }

            int size = timeSlotsForDate.size();
            int totalSessions = 2 * size;
            int bookedSessions = timeSlotsForDate.stream()
                    .mapToInt(TimeSlot::getBookedSessions)
                    .sum();
            boolean isFull = (bookedSessions == totalSessions);

            TimeSlotByDateResponse timeSlotByDateResponse = new TimeSlotByDateResponse();
            timeSlotByDateResponse.setSize(size);
            timeSlotByDateResponse.setTimeSlots(timeSlotsForDate);
            timeSlotByDateResponse.setFull(isFull);
            timeSlotByDateResponse.setTotalSessions(2 * timeSlotByDateResponse.getSize());
            timeSlotByDateResponse.setBookedSessions(timeSlotByDateResponse.getTimeSlots().stream()
                                                                        .mapToInt(TimeSlot::getBookedSessions)
                                                                        .sum());

            responses.add(timeSlotByDateResponse);
        }

        return responses;
    }

    public void save(TimeSlot timeSlot){
        timeSlotRepository.save(timeSlot);
    }

    Optional<TimeSlot> findByDateAndStartTime(LocalDate date, LocalTime startTime){
        return timeSlotRepository.findByDateAndStartTime(date, startTime);
    }


    public class WeekUtils {
        public static LocalDate getStartOfWeek(LocalDate date) {
            return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        public static LocalDate getEndOfWeek(LocalDate date) {
            return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }
    }
}

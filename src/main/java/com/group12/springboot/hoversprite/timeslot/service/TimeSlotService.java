package com.group12.springboot.hoversprite.timeslot.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.timeslot.TimeSlotAPI;
import com.group12.springboot.hoversprite.timeslot.TimeSlotByDateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotByDateResponse;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateRequest;
import com.group12.springboot.hoversprite.timeslot.TimeSlotCreateResponse;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;
import com.group12.springboot.hoversprite.timeslot.entity.TimeSlot;
import com.group12.springboot.hoversprite.timeslot.repository.TimeSlotRepository;

@Service
@Transactional
public class TimeSlotService implements TimeSlotAPI {
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

    @Override
    @Transactional(readOnly = false) // Ensure this is not set to true for write operations
    @PreAuthorize("hasAuthority('APPROVE_BOOKING')")
    public TimeSlotCreateResponse createTimeSlot(TimeSlotCreateRequest request) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findByDateAndStartTime(request.getDate(),
                request.getStartTime());

        if (optionalTimeSlot.isPresent()) {
            throw new IllegalArgumentException("TimeSlot already exists for the given date and start time.");
        }

        TimeSlot newTimeSlot = new TimeSlot(request.getDate(), request.getStartTime(),
                request.getStartTime().plusHours(1));
        newTimeSlot = timeSlotRepository.save(newTimeSlot);

        return new TimeSlotCreateResponse(newTimeSlot);
    }

    @Override
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

    @Override
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

    @Override
    public Optional<TimeSlotDTO> findByDateAndStartTime(LocalDate date, LocalTime startTime) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findByDateAndStartTime(date, startTime);
        return optionalTimeSlot.map(this::convertToDTO); // Convert TimeSlot to TimeSlotDTO
    }

    private TimeSlotDTO convertToDTO(TimeSlot timeSlot) {
        // Assuming you have a constructor in TimeSlotDTO that takes a TimeSlot
        return new TimeSlotDTO(timeSlot);
    }

    @Override
    public TimeSlotDTO findById(Long id) {

        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(id);
        return timeSlot.map(TimeSlotDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS));
    }

    @Override
    public void saveTimeSlot(TimeSlotDTO timeSlotDTO) {
        timeSlotRepository.save(convertToEntity(timeSlotDTO));
    }

    public class WeekUtils {
        public static LocalDate getStartOfWeek(LocalDate date) {
            return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        public static LocalDate getEndOfWeek(LocalDate date) {
            return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }
    }

    private TimeSlot convertToEntity(TimeSlotDTO timeSlotDTO) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(timeSlotDTO.getId());
        timeSlot.setDate(timeSlotDTO.getDate());
        timeSlot.setDayOfWeek(timeSlotDTO.getDayOfWeek());
        timeSlot.setStartTime(timeSlotDTO.getStartTime());
        timeSlot.setEndTime(timeSlotDTO.getEndTime());
        timeSlot.setBookedSessions(timeSlotDTO.getBookedSessions());
        timeSlot.setMaxSessions(timeSlotDTO.getMaxSessions());
        return timeSlot;
    }

    @Override
    public boolean isAvailable(TimeSlotDTO timeSlotDTO) {
        return timeSlotDTO.getBookedSessions() < timeSlotDTO.getMaxSessions();
    }

    @Override
    public void bookSession(TimeSlotDTO timeSlotDTO) {
        if (isAvailable(timeSlotDTO)) {
            TimeSlot timeSlot = convertToEntity(timeSlotDTO);
            timeSlot.setBookedSessions(timeSlot.getBookedSessions() + 1);
            timeSlotRepository.save(timeSlot);
        } else {
            throw new RuntimeException("TimeSlot fully booked");
        }
    }

    @Override
     public void cancelSession(Long timeSlotId) {

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId).orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS));

        timeSlot.setBookedSessions(timeSlot.getBookedSessions()-1);
     }
}
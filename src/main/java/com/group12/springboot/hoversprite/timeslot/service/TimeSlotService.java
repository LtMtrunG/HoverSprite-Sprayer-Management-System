package com.group12.springboot.hoversprite.timeslot.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.group12.springboot.hoversprite.timeslot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
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
    public TimeSlotByDateResponse getTimeSlotByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<TimeSlot> timeSlots = timeSlotRepository.findAllByDate(localDate);

        Set<LocalTime> existingStartTimes = timeSlots.stream()
                .map(TimeSlot::getStartTime)
                .collect(Collectors.toSet());

        Set<LocalTime> missingStartTimes = VALID_TIMES.stream()
                .filter(startTime -> !existingStartTimes.contains(startTime))
                .collect(Collectors.toSet());

        for (LocalTime startTime : missingStartTimes) {
            TimeSlot newTimeSlot = new TimeSlot(localDate, startTime, startTime.plusHours(1));
            timeSlotRepository.save(newTimeSlot);
            timeSlots.add(newTimeSlot);
        }

        // Sort timeSlots based on startTime
        timeSlots.sort(Comparator.comparing(TimeSlot::getStartTime));

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
    public List<TimeSlotByDateResponse> getTimeSlotByWeek(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDate startDate = localDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        List<TimeSlot> allTimeSlots = timeSlotRepository.findAllByDateBetween(startDate, endDate);

        Map<LocalDate, List<TimeSlot>> timeSlotsByDate = allTimeSlots.stream()
                .collect(Collectors.groupingBy(TimeSlot::getDate));

        List<TimeSlotByDateResponse> responses = new ArrayList<>();

        for (LocalDate dateInWeek = startDate; !dateInWeek.isAfter(endDate); dateInWeek = dateInWeek.plusDays(1)) {
            List<TimeSlot> timeSlotsForDate = timeSlotsByDate.getOrDefault(dateInWeek, new ArrayList<>());

            Set<LocalTime> existingStartTimes = timeSlotsForDate.stream()
                    .map(TimeSlot::getStartTime)
                    .collect(Collectors.toSet());

            Set<LocalTime> missingStartTimes = VALID_TIMES.stream()
                    .filter(startTime -> !existingStartTimes.contains(startTime))
                    .collect(Collectors.toSet());

            for (LocalTime startTime : missingStartTimes) {
                TimeSlot newTimeSlot = new TimeSlot(dateInWeek, startTime, startTime.plusHours(1));
                timeSlotRepository.save(newTimeSlot);
                timeSlotsForDate.add(newTimeSlot);
            }

            timeSlotsForDate.sort(Comparator.comparing(TimeSlot::getStartTime));

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
    public List<TimeSlotDTO> getTimeSlotByWeek(LocalDate bookingDate) {
        LocalDate startDate = bookingDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        List<TimeSlot> allTimeSlots = timeSlotRepository.findAllByDateBetween(startDate, endDate);

        return allTimeSlots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TimeSlotDTO> getTimeSlotByDate(LocalDate date) {
        List<TimeSlot> allTimeSlots = timeSlotRepository.findAllByDate(date);

        return allTimeSlots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
                .orElse(null);
    }

    @Override
    public void saveTimeSlot(TimeSlotDTO timeSlotDTO) {
        timeSlotRepository.save(convertToEntity(timeSlotDTO));
    }

    private TimeSlot convertToEntity(TimeSlotDTO timeSlotDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotDTO.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS));
        timeSlot.setBookedSprayersId(timeSlotDTO.getBookedSprayersId());
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

     @Override
     public void setBookedSprayersId(Long timeSlotId, List<Long> bookedSprayersId) {
         TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId).orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS));
         // Get the current list of booked sprayers IDs
         List<Long> existingSprayers = timeSlot.getBookedSprayersId();

         if (existingSprayers == null) {
             // If the list is null, create a new one and add the provided IDs
             existingSprayers = new ArrayList<>(bookedSprayersId);
         } else {
             // If the list is not null, append the provided IDs
             existingSprayers.addAll(bookedSprayersId);
         }

         // Set the updated list back to the TimeSlot entity
         timeSlot.setBookedSprayersId(existingSprayers);
         timeSlotRepository.save(timeSlot);
     }

     public TimeSlotResponse getTimeSlotById(Long id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXISTS));

        return (new TimeSlotResponse(timeSlot));
     }
}
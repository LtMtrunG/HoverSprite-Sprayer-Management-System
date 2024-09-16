package com.group12.springboot.hoversprite.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.field.FieldDTO;
import com.group12.springboot.hoversprite.timeslot.TimeSlotDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {
    private Long id;
    private Long receptionistId;
    private Long farmerId;
    private boolean inProgress;
    private List<String> sprayersName;
    private BookingStatus status;
    private Long timeSlotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private double latitude;
    private double longitude;
    private String cropType;
    private double farmlandArea;
    private double totalCost;

    public BookingResponse(Booking booking, boolean inProgress, List<String> sprayersName, TimeSlotDTO timeSlotDTO, FieldDTO fieldDTO){
        this.id = booking.getId();
        this.receptionistId = booking.getReceptionistId();
        this.farmerId = booking.getFarmerId();
        this.inProgress = inProgress;
        this.sprayersName = sprayersName;
        this.status = booking.getStatus();
        this.timeSlotId = booking.getTimeSlotId();
        this.date = timeSlotDTO.getDate();
        this.startTime = timeSlotDTO.getStartTime();
        this.endTime = timeSlotDTO.getEndTime();
        this.latitude = fieldDTO.getLatitude();
        this.longitude = fieldDTO.getLongitude();
        this.cropType = fieldDTO.getCropType().name();
        this.farmlandArea = fieldDTO.getFarmlandArea();
        this.totalCost = booking.getTotalCost();
    }
}
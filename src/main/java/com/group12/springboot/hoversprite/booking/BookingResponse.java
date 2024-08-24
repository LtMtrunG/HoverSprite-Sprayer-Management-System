package com.group12.springboot.hoversprite.booking;

import java.time.LocalDateTime;
import java.util.List;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.enums.CropType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {
    private Long id;
    private Long receptionistId;
    private Long farmerId;
    private List<Long> sprayersId;
    private CropType cropType;
    private BookingStatus status;
    private double farmlandArea;
    private LocalDateTime createdTime;
    private Long timeSlotId;
    private double totalCost;

    public BookingResponse(Booking booking){
        this.id = booking.getId();
        this.receptionistId = booking.getReceptionistId();
        this.farmerId = booking.getFarmerId();
        this.sprayersId = booking.getSprayersId();
        this.cropType = booking.getCropType();
        this.status = booking.getStatus();
        this.farmlandArea = booking.getFarmlandArea();
        this.createdTime = booking.getCreatedTime();
        this.timeSlotId = booking.getTimeSlotId();
        this.totalCost = booking.getTotalCost();
    }
}
package com.group12.springboot.hoversprite.booking;

import com.group12.springboot.hoversprite.booking.entity.Booking;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.booking.enums.CropType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BookingDTO {

    private final Long id;

    private final Long receptionistId;

    private final Long farmerId;

    private final List<Long> sprayersId;

    private final List<Long> inProgressSprayerIds;

    private final CropType cropType;

    private final BookingStatus status;

    private final double farmlandArea;

    private final LocalDateTime createdTime;

    private final Long timeSlotId;

    private final double totalCost;

    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.receptionistId = booking.getReceptionistId();
        this.farmerId = booking.getFarmerId();
        this.sprayersId = booking.getSprayersId();
        this.inProgressSprayerIds = booking.getInProgressSprayerIds();
        this.cropType = booking.getCropType();
        this.status = booking.getStatus();
        this.farmlandArea = booking.getFarmlandArea();
        this.createdTime = booking.getCreatedTime();
        this.timeSlotId = booking.getTimeSlotId();
        this.totalCost = booking.getTotalCost();
    }
}

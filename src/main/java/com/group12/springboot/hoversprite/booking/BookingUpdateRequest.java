package com.group12.springboot.hoversprite.booking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookingUpdateRequest {
    private Long id;
    private List<Long> sprayersId;
    private Long fieldId;
}
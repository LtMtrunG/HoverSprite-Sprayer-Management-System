package com.group12.springboot.hoversprite.booking;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;;

@Getter
@Setter
@AllArgsConstructor
public class BookingAssignRequest {

    private Long id;

    private List<Long> sprayersId;
}

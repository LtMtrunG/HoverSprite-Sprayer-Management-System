package com.group12.springboot.hoversprite.booking;

import com.group12.springboot.hoversprite.user.SprayerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AvailableSprayersResponse {
    private final SprayerDTO availableSprayer;
}

package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.user.entity.User;
import com.group12.springboot.hoversprite.user.enums.Expertise;
import lombok.Getter;

@Getter
public class SprayerDTO {

    private final Long id;

    private final String fullName;

    private final String phoneNumber;

    private final String email;

    private final Expertise expertise;

    public SprayerDTO(User sprayer) {
        this.id = sprayer.getId();
        this.fullName = sprayer.getFullName();
        this.phoneNumber = sprayer.getPhoneNumber();
        this.email = sprayer.getEmail();
        this.expertise = sprayer.getExpertise();
    }
}

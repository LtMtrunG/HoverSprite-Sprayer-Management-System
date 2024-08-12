package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.user.entity.User;

public class SprayerDTO {

    private final Long id;

    private final String fullName;

    private final String phoneNumber;

    private final String email;

    public SprayerDTO(User sprayer) {
        this.id = sprayer.getId();
        this.fullName = sprayer.getFullName();
        this.phoneNumber = sprayer.getPhoneNumber();
        this.email = sprayer.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}

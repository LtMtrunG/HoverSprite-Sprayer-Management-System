package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.user.entity.User;
import lombok.Getter;

@Getter
public class FarmerDTO {

    private final Long id;

    private final String fullName;

    private final String phoneNumber;

    private final String email;

    private final String address;

    public FarmerDTO(User farmer) {
        this.id = farmer.getId();
        this.fullName = farmer.getFullName();
        this.phoneNumber = farmer.getPhoneNumber();
        this.email = farmer.getEmail();
        this.address = farmer.getAddress();
    }
}

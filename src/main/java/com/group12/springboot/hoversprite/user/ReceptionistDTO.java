package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.user.entity.User;
import lombok.Getter;

@Getter
public class ReceptionistDTO {
    private final Long id;

    private final String fullName;

    private final String phoneNumber;

    private final String email;

    public ReceptionistDTO(User receptionist) {
        this.id = receptionist.getId();
        this.fullName = receptionist.getFullName();
        this.phoneNumber = receptionist.getPhoneNumber();
        this.email = receptionist.getEmail();
    }
}

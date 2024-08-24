package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.NameConstraint;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import com.group12.springboot.hoversprite.validator.StaffEmailConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceptionistCreationRequest {
    @StaffEmailConstraint
    private String email;
    @PasswordConstraint
    private String password;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    private String address;
}
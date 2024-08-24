package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.EmailConstraint;
import com.group12.springboot.hoversprite.validator.NameConstraint;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerCreationRequest {
    @EmailConstraint
    private String email;
    @PasswordConstraint
    private String password;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    private String address;
}
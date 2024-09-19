package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerCreationRequest {
    @EmailConstraint
    private String email;
    @PasswordConstraint
    private String password;
    @PasswordConstraint
    private String confirmPassword;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    @AddressConstraint
    private String address;
}
package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FarmerExternalCreationRequest {
    @EmailConstraint
    private String email;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    @AddressConstraint
    private String address;
}

package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmerUpdatePassword {
    @PasswordConstraint
    private String oldPassword;
    @PasswordConstraint
    private String newPassword;
    @PasswordConstraint
    private String confirmPassword;
}


package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.validator.EmailConstraint;
import com.group12.springboot.hoversprite.validator.NameConstraint;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @PasswordConstraint
    private String password;
    @NameConstraint
    private String fullName;
    @PhoneConstraint
    private String phoneNumber;
    @NonNull
    private String address;
    @Email
    @EmailConstraint
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

}

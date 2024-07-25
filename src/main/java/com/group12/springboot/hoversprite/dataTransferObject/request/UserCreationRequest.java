package com.group12.springboot.hoversprite.dataTransferObject.request;

import com.group12.springboot.hoversprite.validator.PasswordConstraint;
import jakarta.validation.constraints.Size;

public class UserCreationRequest {
    private String email;
    @Size(min = 2, message = "Password must have at least 2 characters.")
    @PasswordConstraint
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

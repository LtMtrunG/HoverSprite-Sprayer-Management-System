package com.group12.springboot.hoversprite.dataTransferObject.request.user;

import com.group12.springboot.hoversprite.entity.enums.Expertise;
import com.group12.springboot.hoversprite.validator.PasswordConstraint;

public class SprayerCreationRequest {
    private String email;
    @PasswordConstraint
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Expertise expertise;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }
}
package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.user.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserOAuth2DTO {

    private String fullName;

    private String phoneNumber;

    private String email;

    @ManyToOne
    private Role role;

    public UserOAuth2DTO(User user) {
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}

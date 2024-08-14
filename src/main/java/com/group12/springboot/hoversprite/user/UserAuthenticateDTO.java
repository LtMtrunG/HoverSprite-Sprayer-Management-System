package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.user.entity.User;

public class UserAuthenticateDTO {
    private final Long id;

    private final String fullName;

    private final String password;

    private final String phoneNumber;

    private final String email;

    private final Role role;

    public UserAuthenticateDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
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

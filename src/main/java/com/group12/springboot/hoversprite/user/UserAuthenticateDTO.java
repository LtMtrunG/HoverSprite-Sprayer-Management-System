package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserAuthenticateDTO {
    private final Long id;

    private final String fullName;

    private final String password;

    private final String phoneNumber;

    private final String email;

    private final Role role;

    private boolean enabled;

    public UserAuthenticateDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}

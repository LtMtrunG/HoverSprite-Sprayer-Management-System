package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private Role role;

    public UserResponse(User user){
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.role = user.getRole();
    }
}
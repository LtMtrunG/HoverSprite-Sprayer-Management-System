package com.group12.springboot.hoversprite.authentication;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {
    private boolean authenticated;
    private String role;
}
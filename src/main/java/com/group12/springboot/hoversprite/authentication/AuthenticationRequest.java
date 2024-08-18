package com.group12.springboot.hoversprite.authentication;
import com.group12.springboot.hoversprite.validator.EmailConstraint;
import com.group12.springboot.hoversprite.validator.PhoneConstraint;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @Email
    @EmailConstraint
    private String email;
    private String password;

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
}
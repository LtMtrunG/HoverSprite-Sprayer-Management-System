package com.group12.springboot.hoversprite.authentication;

public class IntrospectTokenResponse {
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
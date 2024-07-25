package com.group12.springboot.hoversprite.exception;

public enum ErrorCode {
    INVALID_MESSAGE_KEY(1001, "Invalid Message Key"),
    EMAIL_USED(1002, "Email has already been used."),
    INVALID_PASSWORD(1003, "Password must contain at least one capital letter and one special character."),
    EMAIL_NOT_EXISTS(1004, "Email no existed"),
    UNAUTHENTICATED(1005, "Unauthenticated"),
    INVALID_TOKEN(1006, "Invalid Token"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error")
    ;
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

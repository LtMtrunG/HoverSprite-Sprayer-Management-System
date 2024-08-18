package com.group12.springboot.hoversprite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    INVALID_MESSAGE_KEY(1001, "Invalid Message Key", HttpStatus.BAD_REQUEST),
    EMAIL_USED(1002, "Email has already been used.", HttpStatus.BAD_REQUEST),
    INVALID_SIGNUP_INFO(1003, "Field(s) does not meet requirements", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTS(1004, "Email not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "You do not have permission", HttpStatus.FORBIDDEN),
    DAILY_SCHEDULE_CREATED(1007, "The daily schedule already created", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1008, "Invalid Token", HttpStatus.UNAUTHORIZED),
    SESSION_NOT_AVAILABLE(1009, "The selected time slot is not available.", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_EXISTS(1010, "Booking not existed", HttpStatus.NOT_FOUND),
    ACCESS_DENIED(1011, "Access Denied", HttpStatus.FORBIDDEN),
    INVALID_ACTION(1012, "Invalid Action", HttpStatus.BAD_REQUEST),
    RECEPTIONIST_NOT_EXIST(1013, "Receptionist not exist", HttpStatus.NOT_FOUND),
    FARMER_NOT_EXIST(1014, "FARMER not exist", HttpStatus.NOT_FOUND),
    SPRAYER_NOT_EXIST(1015, "SPRAYER not exist", HttpStatus.NOT_FOUND),
    PHONE_NUMBER_NOT_EXISTS(1016, "Phone number not existed", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTS(1017, "User not existed", HttpStatus.NOT_FOUND),
    TIME_SLOT_NOT_EXISTS(1018, "Time slot not existed", HttpStatus.NOT_FOUND),
    SPRAYER_EMPTY(1019, "Sprayers cannot be empty", HttpStatus.BAD_REQUEST),
    SPRAYER_NOT_AVAILABLE(1020, "Sprayer(s) is not available at that time", HttpStatus.BAD_REQUEST),
    SPRAYER_EXPERTISE_NOT_MEET_REQUIREMENTS(1021, "Sprayer(s) expertises not meet requirements", HttpStatus.BAD_REQUEST),
    SPRAYER_EXCEED(1022, "Cannot assign more than 2 sprayers for 1 booking", HttpStatus.BAD_REQUEST),
    SPRAYER_DUPLICATE(1023, "Cannot assign a sprayer twice for a booking", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
    private int code;
    private String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
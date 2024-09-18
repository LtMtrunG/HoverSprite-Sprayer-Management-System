package com.group12.springboot.hoversprite.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_MESSAGE_KEY(1001, "Invalid Message Key", HttpStatus.BAD_REQUEST),
    EMAIL_USED(1002, "Email has already been used", HttpStatus.BAD_REQUEST),
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
    SPRAYER_NOT_ASSIGNED(1024, "This sprayer is not assigned to this booking", HttpStatus.BAD_REQUEST),
    RECEPTIONIST_NOT_RESPONSIBLE(1025, "This receptionist is not responsible for this booking", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_USED(1026, "Phone number has already been used", HttpStatus.BAD_REQUEST),
    EMAIL_PHONE_NOT_EXISTS(1027, "Email and phone number not existed", HttpStatus.NOT_FOUND),
    FARMER_NOT_OWNED(1028, "This farmer does not own this booking", HttpStatus.BAD_REQUEST),
    SPRAYER_ALREADY_IN_PROGRESS(1029, "This sprayer already started working on the booking", HttpStatus.BAD_REQUEST),
    FEEDBACK_NOT_EXISTS(1030, "Feedback not existed", HttpStatus.NOT_FOUND),
    RATING_NOT_VALID(1031, "Rating can only be 1 to 5", HttpStatus.BAD_REQUEST),
    FEEDBACK_GIVEN(1032, "This booking already has feedback", HttpStatus.BAD_REQUEST),
    IMAGES_EXCEED(1033, "Cannot upload more than 5 images", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1034, "Email must end with .com or .vn", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_COUNTRIES(1035, "Address must be within Vietnam", HttpStatus.BAD_REQUEST),
    FIELD_EXCEED(1036, "A farmer can only have maximum 10 fields", HttpStatus.BAD_REQUEST),
    FIELD_NOT_EXIST(1037, "Field not exist", HttpStatus.NOT_FOUND),
    FARMER_NOT_OWN_FIELD(1038, "The farmer does not own this field", HttpStatus.BAD_REQUEST),
    FIELD_IN_BOOKING(1039, "The field is having a spraying section", HttpStatus.BAD_REQUEST),
    INVALID_CROP_TYPE(1040, "The crop type is invalid", HttpStatus.NOT_ACCEPTABLE),
    NOTIFICATION_NOT_EXISTS(1041, "The notification not existed", HttpStatus.NOT_FOUND),
    BOOKING_NOT_COMPLETE(1042, "The booking is not completed yet", HttpStatus.NOT_FOUND),
    KEYWORD_NOT_FOUND(1043, "The booking ID is not found, or the key word is invalid", HttpStatus.NOT_FOUND),
    FIELD_NAME_ALREADY_EXISTS(1044, "The field with same name already exists", HttpStatus.BAD_REQUEST),
    FIELD_LOCATION_EXISTS(1045, "The field with same location already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1046, "The old password is not correct", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1047, "The new password and password confirmation are not matched", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private int code;
    private String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
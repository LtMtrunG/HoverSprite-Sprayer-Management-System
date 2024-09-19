package com.group12.springboot.hoversprite.exception;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus; // <-- Make sure this import is present

import com.group12.springboot.hoversprite.common.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleRuntimeException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ApiResponse> handleCustomException(CustomException exception) {
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode = exception.getErrorCode();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ApiResponse apiResponse = new ApiResponse();
        List<String> errorMessages = new ArrayList<>();

        // Using AtomicReference to hold the ErrorCode
        AtomicReference<ErrorCode> errorCode = new AtomicReference<>(ErrorCode.INVALID_MESSAGE_KEY);

        // ErrorCode errorCode = ErrorCode.INVALID_MESSAGE_KEY;

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();

            System.out.println(field);
            switch (field) {
                case "password", "email", "fullName", "phoneNumber", "address", "oldPassword", "newPassword", "confirmPassword":
                    errorCode.set(ErrorCode.INVALID_SIGNUP_INFO);
                    break;
                default:
                    errorCode.set(ErrorCode.INVALID_MESSAGE_KEY); // Default error code
            }

            // Add the error message for each field
            errorMessages.add(fieldError.getDefaultMessage());
        });

        apiResponse.setCode(errorCode.get().getCode()); // A generic validation error code
        apiResponse.setMessage(String.join("; ", errorMessages)); // Join all error messages into a single string

        return ResponseEntity.status(errorCode.get().getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handleParseException(ParseException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        apiResponse.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNAUTHORIZED.getCode());
        apiResponse.setMessage(ErrorCode.UNAUTHORIZED.getMessage());
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<ApiResponse> handlingIllegalArgumentException(IllegalArgumentException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.DAILY_SCHEDULE_CREATED.getCode());
        apiResponse.setMessage(ErrorCode.DAILY_SCHEDULE_CREATED.getMessage());
        return ResponseEntity.status(ErrorCode.DAILY_SCHEDULE_CREATED.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse> handlingConstraintViolationException(ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations().iterator().next().getMessage();
        System.out.println(errorMessage);
        if (errorMessage.contains("Email")) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setCode(ErrorCode.INVALID_EMAIL_FORMAT.getCode());
            apiResponse.setMessage(errorMessage);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.RATING_NOT_VALID.getCode());
        apiResponse.setMessage(ErrorCode.RATING_NOT_VALID.getMessage());
        return ResponseEntity.status(ErrorCode.RATING_NOT_VALID.getStatusCode()).body(apiResponse);
    }

    // Add exception handler for HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_CROP_TYPE.getCode());
        apiResponse.setMessage(ErrorCode.INVALID_CROP_TYPE.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
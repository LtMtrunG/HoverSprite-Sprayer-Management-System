package com.group12.springboot.hoversprite.exception;

import com.group12.springboot.hoversprite.dataTransferObject.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;
import java.util.Set;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleRuntimeException(Exception exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ApiResponse> handleCustomException(CustomException exception){
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode = exception.getErrorCode();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handleParseException(ParseException exception){
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

    @ExceptionHandler(value = AuthenticationServiceException.class)
    ResponseEntity<ApiResponse> handlingAuthenticationServiceException(AuthenticationServiceException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_TOKEN.getCode());
        apiResponse.setMessage(ErrorCode.INVALID_TOKEN.getMessage());
        return ResponseEntity.status(ErrorCode.INVALID_TOKEN.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = JwtException.class)
    ResponseEntity<ApiResponse> handlingJwtException(JwtException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_TOKEN.getCode());
        apiResponse.setMessage(ErrorCode.INVALID_TOKEN.getMessage());
        return ResponseEntity.status(ErrorCode.INVALID_TOKEN.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        StringBuilder errors = new StringBuilder();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                String field = fieldError.getField();

                String defaultMessage = fieldError.getDefaultMessage();
                errors.append(defaultMessage);
            }
        });

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_MESSAGE_KEY.getCode());
        apiResponse.setMessage(errors.toString().trim());

        return ResponseEntity.status(ErrorCode.INVALID_MESSAGE_KEY.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse> handlingConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        String firstErrorMessage = violations.isEmpty() ? "Unknown validation error" : violations.iterator().next().getMessage();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.INVALID_MESSAGE_KEY.getCode());
        apiResponse.setMessage(firstErrorMessage);

        return ResponseEntity.status(ErrorCode.INVALID_MESSAGE_KEY.getStatusCode()).body(apiResponse);
    }
}

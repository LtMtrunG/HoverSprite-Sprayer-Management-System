package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StaffEmailValidator implements ConstraintValidator<StaffEmailConstraint, String> {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@hoversprite\\.com$";

    @Override
    public void initialize(StaffEmailConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Validate the email address against the pattern
        return email.matches(EMAIL_PATTERN);
    }
}
package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|vn)$";

    @Override
    public void initialize(EmailConstraint constraintAnnotation) {
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

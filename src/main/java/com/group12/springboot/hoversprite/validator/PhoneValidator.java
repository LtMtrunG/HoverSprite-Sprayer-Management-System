package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return false;
        }

        // Remove any whitespace from the phone number
        String sanitizedPhone = phone.replaceAll("\\s", "");

        // Validate the sanitized phone number
        return sanitizedPhone.matches("^(0\\d{9}|\\+84\\d{9})$");
    }
}

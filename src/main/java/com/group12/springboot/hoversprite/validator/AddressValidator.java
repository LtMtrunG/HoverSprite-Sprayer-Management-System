package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AddressValidator implements ConstraintValidator<AddressConstraint, String> {

    @Override
    public void initialize(AddressConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return ((value.contains("Vietnam") || value.contains("Viet Nam")
                || value.contains("Việt Nam")));
    }
}

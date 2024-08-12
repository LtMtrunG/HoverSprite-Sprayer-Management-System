package com.group12.springboot.hoversprite.validator;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<DateConstraint, LocalDate> {
    @Override
    public void initialize(DateConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime == null) {
            return false;
        }
        return true;
        // return dateTime.isAfter(ChronoLocalDate.from(LocalDateTime.now().plusHours(6)));
    }
}
package com.group12.springboot.hoversprite.validator;

import com.group12.springboot.hoversprite.constraint.DateConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;

public class DateValidator implements ConstraintValidator<DateConstraint, LocalDate> {
    @Override
    public void initialize(DateConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(ChronoLocalDate.from(LocalDateTime.now().plusHours(6)));
    }
}

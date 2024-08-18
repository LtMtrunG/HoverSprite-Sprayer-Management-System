package com.group12.springboot.hoversprite.validator;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartTimeValidator implements ConstraintValidator<StartTimeConstraint, LocalTime> {
    private static final Set<LocalTime> VALID_TIMES = new HashSet<>();

    static {
        VALID_TIMES.add(LocalTime.of(4, 0));
        VALID_TIMES.add(LocalTime.of(5, 0));
        VALID_TIMES.add(LocalTime.of(6, 0));
        VALID_TIMES.add(LocalTime.of(7, 0));
        VALID_TIMES.add(LocalTime.of(16, 0));
        VALID_TIMES.add(LocalTime.of(17, 0));
    }

    @Override
    public void initialize(StartTimeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalTime startTime, ConstraintValidatorContext constraintValidatorContext) {
        return startTime == null || VALID_TIMES.contains(startTime);
    }
}
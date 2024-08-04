package com.group12.springboot.hoversprite.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.group12.springboot.hoversprite.validator.StartTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = StartTimeValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface StartTimeConstraint {
    String message() default "Invalid Time Slot.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

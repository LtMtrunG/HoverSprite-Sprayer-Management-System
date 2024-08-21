package com.group12.springboot.hoversprite.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StaffEmailValidator.class)
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffEmailConstraint {
    String message() default "The domain part must be hoversprite.com";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

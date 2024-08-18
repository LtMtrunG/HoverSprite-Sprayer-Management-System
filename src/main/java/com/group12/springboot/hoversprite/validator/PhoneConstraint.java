package com.group12.springboot.hoversprite.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneConstraint {
    String message() default "Phone number must start with 0 or +84, follow by 9 or 10 digits and can include whitespace";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

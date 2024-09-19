package com.group12.springboot.hoversprite.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameConstraint {
    String message() default "Each word should start with capital letters, up to two non-adjacent capitalized letters are allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

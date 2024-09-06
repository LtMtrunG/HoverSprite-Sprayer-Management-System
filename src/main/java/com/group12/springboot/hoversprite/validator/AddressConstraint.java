package com.group12.springboot.hoversprite.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = AddressValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface AddressConstraint {
    String message() default "Our service is only available in Vietnam for now.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

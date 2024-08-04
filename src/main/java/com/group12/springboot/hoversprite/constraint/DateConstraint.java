package com.group12.springboot.hoversprite.constraint;

import com.group12.springboot.hoversprite.validator.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = DateValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface DateConstraint {
    String message() default "Invalid Date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

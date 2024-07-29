package com.group12.springboot.hoversprite.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.group12.springboot.hoversprite.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface PasswordConstraint {
    String message() default "Password must contain at least one capital letter and one special character.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {
    @Override
    public void initialize(EmailConstraint constraintAnnotation) {
    }
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email == null) {
            return false;
        }
        return (email.matches(".+(.com|.vn)$"));
    }
}

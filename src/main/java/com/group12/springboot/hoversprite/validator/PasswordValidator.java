package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        boolean hasCapitalLetter = password.chars().anyMatch(Character::isUpperCase);
        boolean hasSpecialCharacter = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        boolean has2Characters = password.length() > 2;
        return hasCapitalLetter && hasSpecialCharacter && has2Characters;
    }
}

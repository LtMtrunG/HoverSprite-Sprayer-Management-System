package com.group12.springboot.hoversprite.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<NameConstraint, String> {
    @Override
    public void initialize(NameConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return false;
        }

        // Split the name into words
        String[] words = name.split("\\s+");

        for (String word : words) {
            if (!isValidWord(word)) {
                return false; // If any word is invalid, return false
            }
        }

        // All words are valid
        return true;
    }

    private boolean isValidWord(String word) {
        int capitalCount = 0;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            // Check if the character is uppercase
            if (Character.isUpperCase(c)) {
                capitalCount++;

                // More than two capital letters are not allowed
                if (capitalCount > 2) {
                    return false;
                }

                // Adjacent capital letters are not allowed
                if (i > 0 && Character.isUpperCase(word.charAt(i - 1))) {
                    return false;
                }
            }

            // Ensure the first letter is uppercase and the rest (besides a second capital) are lowercase
            if (i == 0 && !Character.isUpperCase(c)) {
                return false;
            } else if (i > 0 && capitalCount == 1 && !Character.isLowerCase(c) && !Character.isUpperCase(c)) {
                // Ensure the letters between the two capitals are lowercase
                return false;
            }
        }

        return true; // The word is valid if we haven't encountered any invalid conditions
    }
}
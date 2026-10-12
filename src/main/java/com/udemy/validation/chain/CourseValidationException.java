package com.udemy.validation.chain;

public class CourseValidationException extends IllegalStateException {
    public CourseValidationException(String message) {
        super(message);
    }
}

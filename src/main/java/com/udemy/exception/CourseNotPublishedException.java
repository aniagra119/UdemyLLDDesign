package com.udemy.exception;

public class CourseNotPublishedException extends RuntimeException {
    public CourseNotPublishedException(String message) {
        super(message);
    }
}

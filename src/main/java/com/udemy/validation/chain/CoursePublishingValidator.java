package com.udemy.validation.chain;

import com.udemy.model.Course;

public interface CoursePublishingValidator {
    void setNext(CoursePublishingValidator nextValidator);
    void validate(Course course) throws CourseValidationException;
}

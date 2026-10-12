package com.udemy.validation.chain;

import com.udemy.model.Course;

public abstract class AbstractCoursePublishingValidator implements CoursePublishingValidator {
    protected CoursePublishingValidator next;

    @Override
    public void setNext(CoursePublishingValidator nextValidator) {
        this.next = nextValidator;
    }

    protected void passToNext(Course course) {
        if (next != null) {
            next.validate(course);
        }
    }
}

package com.udemy.validation.chain;

import com.udemy.model.Course;

public class CurriculumCompletenessValidator extends AbstractCoursePublishingValidator {

    @Override
    public void validate(Course course) throws CourseValidationException {
        if (course.getModules().isEmpty()) {
            throw new CourseValidationException("Course must contain at least one module before publishing.");
        }

        if (course.getLectureCount() == 0) {
            throw new CourseValidationException("Course must contain at least one lecture before publishing.");
        }

        passToNext(course);
    }
}

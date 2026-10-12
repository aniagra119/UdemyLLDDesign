package com.udemy.validation.chain;

import com.udemy.model.Course;

public class InstructorValidator extends AbstractCoursePublishingValidator {

    @Override
    public void validate(Course course) throws CourseValidationException {
        if (course.getInstructors().isEmpty()) {
            throw new CourseValidationException("Course must have at least one assigned instructor to be published.");
        }

        double totalShare = course.getAuthorships().stream()
                .mapToDouble(a -> a.getRevenueSharePercentage())
                .sum();

        if (Math.abs(totalShare - 100.0) > 0.01) {
            throw new CourseValidationException("Total instructor revenue share must equal exactly 100%, currently: " + totalShare + "%");
        }

        passToNext(course);
    }
}

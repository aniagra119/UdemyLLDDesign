package com.udemy.validation.chain;

import com.udemy.model.Course;
import com.udemy.model.Lecture;
import com.udemy.model.Module;

public class VideoQualityValidator extends AbstractCoursePublishingValidator {

    @Override
    public void validate(Course course) throws CourseValidationException {
        for (Module m : course.getModules()) {
            for (Lecture l : m.getLectures()) {
                if (l.getVideo() == null) {
                    throw new CourseValidationException("Lecture '" + l.getTitle() + "' in module '" + m.getTitle() + "' is missing video content.");
                }
                if (l.getVideo().getDurationSeconds() <= 0) {
                    throw new CourseValidationException("Lecture '" + l.getTitle() + "' must have video duration greater than 0 seconds.");
                }
            }
        }

        passToNext(course);
    }
}

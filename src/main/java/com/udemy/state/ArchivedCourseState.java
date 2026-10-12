package com.udemy.state;

import com.udemy.enums.CourseStatus;
import com.udemy.model.Course;
import com.udemy.model.Module;
import com.udemy.model.Student;

public class ArchivedCourseState implements CourseState {

    @Override
    public void addModule(Course course, Module module) {
        throw new IllegalStateException("Cannot add modules to an ARCHIVED course: " + course.getTitle());
    }

    @Override
    public void publish(Course course) {
        throw new IllegalStateException("Cannot publish an ARCHIVED course directly. Please duplicate or re-review.");
    }

    @Override
    public void archive(Course course) {
        // Idempotent
    }

    @Override
    public void enrollStudent(Course course, Student student) {
        throw new IllegalStateException("Cannot enroll in course '" + course.getTitle() + "' because it has been ARCHIVED.");
    }

    @Override
    public CourseStatus getStatus() {
        return CourseStatus.ARCHIVED;
    }
}

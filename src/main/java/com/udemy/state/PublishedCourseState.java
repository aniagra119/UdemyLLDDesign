package com.udemy.state;

import com.udemy.enums.CourseStatus;
import com.udemy.model.Course;
import com.udemy.model.Module;
import com.udemy.model.Student;

public class PublishedCourseState implements CourseState {

    @Override
    public void addModule(Course course, Module module) {
        throw new IllegalStateException("Cannot modify the curriculum of course '" + course.getTitle() + "' because it is already PUBLISHED with active students.");
    }

    @Override
    public void publish(Course course) {
        // Idempotent: already published
    }

    @Override
    public void archive(Course course) {
        course.setState(new ArchivedCourseState());
    }

    @Override
    public void enrollStudent(Course course, Student student) {
        // Enrollment allowed in PUBLISHED state
    }

    @Override
    public CourseStatus getStatus() {
        return CourseStatus.PUBLISHED;
    }
}

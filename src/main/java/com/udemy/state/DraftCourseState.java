package com.udemy.state;

import com.udemy.enums.CourseStatus;
import com.udemy.exception.CourseNotPublishedException;
import com.udemy.model.Course;
import com.udemy.model.Module;
import com.udemy.model.Student;

public class DraftCourseState implements CourseState {

    @Override
    public void addModule(Course course, Module module) {
        course.doAddModule(module);
    }

    @Override
    public void publish(Course course) {
        course.getValidationPipeline().validate(course);
        course.setState(new PublishedCourseState());
    }

    @Override
    public void archive(Course course) {
        course.setState(new ArchivedCourseState());
    }

    @Override
    public void enrollStudent(Course course, Student student) {
        throw new CourseNotPublishedException("Cannot enroll in course '" + course.getTitle() + "' because it is currently in DRAFT status.");
    }

    @Override
    public CourseStatus getStatus() {
        return CourseStatus.DRAFT;
    }
}

package com.udemy.state;

import com.udemy.enums.CourseStatus;
import com.udemy.model.Course;
import com.udemy.model.Module;
import com.udemy.model.Student;

public interface CourseState {
    void addModule(Course course, Module module);
    void publish(Course course);
    void archive(Course course);
    void enrollStudent(Course course, Student student);
    CourseStatus getStatus();
}

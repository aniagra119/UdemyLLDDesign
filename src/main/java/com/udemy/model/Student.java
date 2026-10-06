package com.udemy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends User {
    private final List<String> enrolledCourseIds;

    public Student(String id, String email, String fullName) {
        super(id, email, fullName);
        this.enrolledCourseIds = new ArrayList<>();
    }

    public Student(String email, String fullName) {
        super(email, fullName);
        this.enrolledCourseIds = new ArrayList<>();
    }

    public List<String> getEnrolledCourseIds() {
        return Collections.unmodifiableList(enrolledCourseIds);
    }

    public void addEnrolledCourse(String courseId) {
        if (courseId != null && !enrolledCourseIds.contains(courseId)) {
            enrolledCourseIds.add(courseId);
            markUpdated();
        }
    }

    public boolean isEnrolled(String courseId) {
        return enrolledCourseIds.contains(courseId);
    }
}

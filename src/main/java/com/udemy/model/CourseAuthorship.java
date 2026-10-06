package com.udemy.model;

import com.udemy.enums.AuthorshipRole;
import java.util.Objects;

public class CourseAuthorship extends BaseEntity {
    private final String courseId;
    private final Instructor instructor;
    private final AuthorshipRole role;
    private double revenueSharePercentage;

    public CourseAuthorship(String courseId, Instructor instructor, AuthorshipRole role, double revenueSharePercentage) {
        super();
        this.courseId = Objects.requireNonNull(courseId, "courseId must not be null");
        this.instructor = Objects.requireNonNull(instructor, "instructor must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        setRevenueSharePercentage(revenueSharePercentage);
    }

    public String getCourseId() {
        return courseId;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public AuthorshipRole getRole() {
        return role;
    }

    public double getRevenueSharePercentage() {
        return revenueSharePercentage;
    }

    public void setRevenueSharePercentage(double percentage) {
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("Revenue share percentage must be between 0 and 100, got: " + percentage);
        }
        this.revenueSharePercentage = percentage;
        markUpdated();
    }
}

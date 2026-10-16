package com.udemy.addon;

import com.udemy.model.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BaseCourseProduct implements CourseProduct {
    private final Course course;

    public BaseCourseProduct(Course course) {
        this.course = Objects.requireNonNull(course, "course must not be null");
    }

    @Override
    public Course getCourse() {
        return course;
    }

    @Override
    public String getDescription() {
        return course.getTitle();
    }

    @Override
    public double getPrice() {
        return course.getBasePrice();
    }

    @Override
    public List<String> getEntitlements() {
        List<String> list = new ArrayList<>();
        list.add("STANDARD_VIDEO_STREAMING");
        return Collections.unmodifiableList(list);
    }
}

package com.udemy.addon;

import com.udemy.model.Course;

import java.util.List;

public interface CourseProduct {
    Course getCourse();
    String getDescription();
    double getPrice();
    List<String> getEntitlements();
}

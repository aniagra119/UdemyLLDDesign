package com.udemy.service;

import com.udemy.exception.CourseNotPublishedException;
import com.udemy.exception.ResourceNotFoundException;
import com.udemy.model.Course;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CourseCatalogService {
    private final Map<String, Course> courseRegistry = new ConcurrentHashMap<>();

    public void registerCourse(Course course) {
        if (course != null) {
            courseRegistry.put(course.getId(), course);
        }
    }

    public Course getCourse(String courseId) {
        return Optional.ofNullable(courseRegistry.get(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }

    public List<Course> getPublishedCourses() {
        return courseRegistry.values().stream()
                .filter(Course::isPublished)
                .toList();
    }

    public void publishCourse(String courseId) {
        Course course = getCourse(courseId);
        course.publish();
    }

    public List<Course> getAllCourses() {
        return Collections.unmodifiableList(List.copyOf(courseRegistry.values()));
    }
}

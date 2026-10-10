package com.udemy.service;

import com.udemy.exception.CourseNotPublishedException;
import com.udemy.model.Course;
import com.udemy.model.Student;
import com.udemy.progress.Enrollment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EnrollmentService {
    // Key: "studentId:courseId" -> Enrollment
    private final Map<String, Enrollment> enrollmentMap = new ConcurrentHashMap<>();

    public synchronized Enrollment enroll(Student student, Course course) {
        if (!course.isPublished()) {
            throw new CourseNotPublishedException("Cannot enroll in unpublished course: " + course.getTitle());
        }

        String key = student.getId() + ":" + course.getId();
        if (enrollmentMap.containsKey(key)) {
            return enrollmentMap.get(key); // Idempotent enrollment
        }

        Enrollment enrollment = new Enrollment(student, course);
        enrollmentMap.put(key, enrollment);
        return enrollment;
    }

    public Optional<Enrollment> getEnrollment(String studentId, String courseId) {
        return Optional.ofNullable(enrollmentMap.get(studentId + ":" + courseId));
    }

    public List<Enrollment> getEnrollmentsForStudent(String studentId) {
        return enrollmentMap.values().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .toList();
    }
}

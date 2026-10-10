package com.udemy.service;

import com.udemy.exception.InvalidRatingException;
import com.udemy.exception.UnenrolledStudentException;
import com.udemy.model.Course;
import com.udemy.model.Review;
import com.udemy.model.Student;

import java.util.Objects;

public class ReviewService {

    public synchronized Review submitReview(Student student, Course course, double rating, String feedbackComment) {
        Objects.requireNonNull(student, "student must not be null");
        Objects.requireNonNull(course, "course must not be null");

        // Invariant 1: Verified purchase constraint (only enrolled students can review)
        if (!student.isEnrolled(course.getId())) {
            throw new UnenrolledStudentException("Student " + student.getFullName() + " is not enrolled in course " + course.getTitle());
        }

        // Invariant 2: Rating bounds check
        if (rating < 1.0 || rating > 5.0) {
            throw new InvalidRatingException("Rating must be between 1.0 and 5.0, got: " + rating);
        }

        Review review = new Review(student, course.getId(), rating, feedbackComment);
        course.addReview(review);
        return review;
    }
}

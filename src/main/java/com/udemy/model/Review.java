package com.udemy.model;

import java.util.Objects;

public class Review extends BaseEntity {
    private final Student student;
    private final String courseId;
    private double rating; // Clamped 1.0 to 5.0
    private String feedbackComment;

    public Review(Student student, String courseId, double rating, String feedbackComment) {
        super();
        this.student = Objects.requireNonNull(student, "student must not be null");
        this.courseId = Objects.requireNonNull(courseId, "courseId must not be null");
        setRating(rating);
        this.feedbackComment = feedbackComment != null ? feedbackComment : "";
    }

    public Student getStudent() {
        return student;
    }

    public String getCourseId() {
        return courseId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0, got: " + rating);
        }
        this.rating = rating;
        markUpdated();
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
        markUpdated();
    }
}

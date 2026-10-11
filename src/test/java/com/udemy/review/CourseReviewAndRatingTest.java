package com.udemy.review;

import com.udemy.enums.AuthorshipRole;
import com.udemy.exception.InvalidRatingException;
import com.udemy.exception.UnenrolledStudentException;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Review;
import com.udemy.model.Student;
import com.udemy.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course Review & Moving Average Rating System Tests")
class CourseReviewAndRatingTest {

    private Course course;
    private Student enrolledStudent1;
    private Student enrolledStudent2;
    private Student unenrolledStudent;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Instructor instructor = new Instructor("martin@craftsman.com", "Robert C. Martin", "Clean Architecture");
        course = new Course("Clean Architecture & SOLID in Java 21", "Enterprise domain engineering", 2999.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        Module m1 = new Module("Foundations", 1);
        m1.addLecture(new Lecture("Lec 1", 1, new VideoContent("http://cdn/clean.mp4", 1200)));
        course.addModule(m1);
        course.publish();

        enrolledStudent1 = new Student("s1@edu.com", "Student One");
        enrolledStudent1.addEnrolledCourse(course.getId());

        enrolledStudent2 = new Student("s2@edu.com", "Student Two");
        enrolledStudent2.addEnrolledCourse(course.getId());

        unenrolledStudent = new Student("s3@edu.com", "Unenrolled Intruder");

        reviewService = new ReviewService();
    }

    @Test
    @DisplayName("Verified Purchase Invariant: Unenrolled students are rejected from rating")
    void testUnenrolledStudentCannotRate() {
        assertThrows(UnenrolledStudentException.class, () ->
                reviewService.submitReview(unenrolledStudent, course, 5.0, "Great preview!"));
    }

    @Test
    @DisplayName("Rating Range Validation: Ratings must be between 1.0 and 5.0 stars")
    void testRatingBoundsValidation() {
        assertThrows(InvalidRatingException.class, () ->
                reviewService.submitReview(enrolledStudent1, course, 0.5, "Too low"));

        assertThrows(InvalidRatingException.class, () ->
                reviewService.submitReview(enrolledStudent1, course, 5.5, "Too high"));
    }

    @Test
    @DisplayName("Moving Average: Course updates moving average and rating count as reviews arrive")
    void testMovingAverageRatingCalculation() {
        assertEquals(0.0, course.getAverageRating());
        assertEquals(0, course.getTotalRatingsCount());

        // Student 1 rates 5.0
        reviewService.submitReview(enrolledStudent1, course, 5.0, "Phenomenal deep dive!");
        assertEquals(5.0, course.getAverageRating());
        assertEquals(1, course.getTotalRatingsCount());

        // Student 2 rates 4.0 -> average = 4.5
        reviewService.submitReview(enrolledStudent2, course, 4.0, "Very solid examples.");
        assertEquals(4.5, course.getAverageRating());
        assertEquals(2, course.getTotalRatingsCount());

        // Student 2 updates rating to 5.0 (idempotent single review per student)
        reviewService.submitReview(enrolledStudent2, course, 5.0, "Revised: absolutely flawless!");
        assertEquals(5.0, course.getAverageRating());
        assertEquals(2, course.getTotalRatingsCount(), "Total ratings count must remain 2 after update");
    }
}

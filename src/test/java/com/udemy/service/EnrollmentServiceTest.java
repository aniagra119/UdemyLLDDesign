package com.udemy.service;

import com.udemy.enums.AuthorshipRole;
import com.udemy.exception.CourseNotPublishedException;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.progress.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Enrollment Service & Invariant Tests")
class EnrollmentServiceTest {

    private EnrollmentService enrollmentService;
    private Course publishedCourse;
    private Course draftCourse;
    private Student student;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService();
        student = new Student("stud@test.com", "Student Test");

        Instructor instructor = new Instructor("inst@test.com", "Instructor Test", "Testing");

        publishedCourse = new Course("Published Course", "Desc", 999.0);
        publishedCourse.addAuthorship(new CourseAuthorship(publishedCourse.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));
        Module mod = new Module("Mod 1", 1);
        mod.addLecture(new Lecture("Lec 1", 1, new VideoContent("http://cdn/v.mp4", 300)));
        publishedCourse.addModule(mod);
        publishedCourse.publish();

        draftCourse = new Course("Draft Course", "Desc", 999.0);
    }

    @Test
    @DisplayName("Enrollment Invariant: Cannot enroll in unpublished or draft course")
    void testCannotEnrollInUnpublishedCourse() {
        assertThrows(CourseNotPublishedException.class, () -> enrollmentService.enroll(student, draftCourse));
    }

    @Test
    @DisplayName("Enrollment Idempotence: Enrolling multiple times returns the same existing enrollment")
    void testEnrollmentIdempotence() {
        Enrollment enrollment1 = enrollmentService.enroll(student, publishedCourse);
        assertNotNull(enrollment1);

        Enrollment enrollment2 = enrollmentService.enroll(student, publishedCourse);
        assertSame(enrollment1, enrollment2, "Enrolling twice must return identical active enrollment instance");

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(student.getId());
        assertEquals(1, enrollments.size());
    }

    @Test
    @DisplayName("Enrollment Query: Returns Optional.empty() when student is not enrolled")
    void testGetEnrollmentNotEnrolled() {
        Optional<Enrollment> opt = enrollmentService.getEnrollment("unknown-student", publishedCourse.getId());
        assertTrue(opt.isEmpty());
    }
}

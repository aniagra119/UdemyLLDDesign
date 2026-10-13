package com.udemy.state;

import com.udemy.enums.AuthorshipRole;
import com.udemy.enums.CourseStatus;
import com.udemy.exception.CourseNotPublishedException;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("State Pattern: Course Lifecycle & Access Control Tests")
class CourseStatePatternTest {

    private Course course;
    private Student student;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        instructor = new Instructor("lead@tech.com", "Anirudh Agrawal", "Systems");
        course = new Course("Mastering GoF Patterns", "Enterprise architecture in Java 21", 2499.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        Module m1 = new Module("State Machine Module", 1);
        m1.addLecture(new Lecture("Lec 1", 1, new VideoContent("http://cdn/v1.mp4", 600)));
        course.addModule(m1);

        student = new Student("stud@test.com", "Learner");
    }

    @Test
    @DisplayName("Draft State: Can add modules, but enrollment throws CourseNotPublishedException")
    void testDraftCourseState() {
        assertEquals(CourseStatus.DRAFT, course.getStatus());
        assertTrue(course.getState() instanceof DraftCourseState);

        // Can add more modules in draft
        Module m2 = new Module("Draft Module 2", 2);
        m2.addLecture(new Lecture("Lec 2", 1, new VideoContent("http://cdn/v2.mp4", 400)));
        assertDoesNotThrow(() -> course.addModule(m2));
        assertEquals(2, course.getModules().size());

        // Cannot enroll students while in draft
        assertThrows(CourseNotPublishedException.class, () -> course.enroll(student));
    }

    @Test
    @DisplayName("Published State: Allows student enrollment, but locks curriculum modification")
    void testPublishedCourseState() {
        course.publish();
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
        assertTrue(course.getState() instanceof PublishedCourseState);

        // Student enrollment succeeds
        assertDoesNotThrow(() -> course.enroll(student));

        // Modifying curriculum of live course throws IllegalStateException to prevent corrupting active learner progress
        Module lateModule = new Module("Unauthorized Modification", 3);
        assertThrows(IllegalStateException.class, () -> course.addModule(lateModule));
    }

    @Test
    @DisplayName("Archived State: Rejects both new enrollments and curriculum modifications")
    void testArchivedCourseState() {
        course.publish();
        course.archive();
        assertEquals(CourseStatus.ARCHIVED, course.getStatus());
        assertTrue(course.getState() instanceof ArchivedCourseState);

        // Cannot add modules
        Module m = new Module("Late Module", 2);
        assertThrows(IllegalStateException.class, () -> course.addModule(m));

        // Cannot re-publish directly
        assertThrows(IllegalStateException.class, () -> course.publish());

        // Cannot enroll new students
        assertThrows(IllegalStateException.class, () -> course.enroll(student));
    }
}

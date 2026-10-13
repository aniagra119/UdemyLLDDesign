package com.udemy.validation.chain;

import com.udemy.enums.AuthorshipRole;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Chain of Responsibility: Course Publishing Quality Audit Tests")
class CoursePublishingValidationChainTest {

    private Course course;
    private Instructor primaryInstructor;
    private Instructor coInstructor;
    private CourseValidationPipeline pipeline;

    @BeforeEach
    void setUp() {
        course = new Course("Quality Pipeline Deep Dive", "Audit chains", 1999.0);
        primaryInstructor = new Instructor("lead@tech.com", "Anirudh Agrawal", "Architecture");
        coInstructor = new Instructor("co@tech.com", "Co-Author", "DevOps");
        pipeline = new CourseValidationPipeline();
    }

    @Test
    @DisplayName("Instructor Validator: Fails if course has no instructors")
    void testFailsWhenNoInstructors() {
        Module m = new Module("M1", 1);
        m.addLecture(new Lecture("L1", 1, new VideoContent("http://v.mp4", 100)));
        course.addModule(m);

        CourseValidationException ex = assertThrows(CourseValidationException.class, () -> pipeline.validate(course));
        assertTrue(ex.getMessage().contains("at least one assigned instructor"));
    }

    @Test
    @DisplayName("Instructor Validator: Fails if revenue share percentages do not total 100%")
    void testFailsWhenRevenueShareNotHundredPercent() {
        // Total = 70 + 20 = 90% (missing 10%)
        course.addAuthorship(new CourseAuthorship(course.getId(), primaryInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 70.0));
        course.addAuthorship(new CourseAuthorship(course.getId(), coInstructor, AuthorshipRole.CO_INSTRUCTOR, 20.0));

        Module m = new Module("M1", 1);
        m.addLecture(new Lecture("L1", 1, new VideoContent("http://v.mp4", 100)));
        course.addModule(m);

        CourseValidationException ex = assertThrows(CourseValidationException.class, () -> pipeline.validate(course));
        assertTrue(ex.getMessage().contains("Total instructor revenue share must equal exactly 100%"));
    }

    @Test
    @DisplayName("Curriculum Completeness: Fails if course contains no modules")
    void testFailsWhenNoModules() {
        course.addAuthorship(new CourseAuthorship(course.getId(), primaryInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        CourseValidationException ex = assertThrows(CourseValidationException.class, () -> pipeline.validate(course));
        assertTrue(ex.getMessage().contains("at least one module"));
    }

    @Test
    @DisplayName("Video Quality Validator: Fails if lecture has zero or negative video duration")
    void testFailsWhenVideoDurationZero() {
        course.addAuthorship(new CourseAuthorship(course.getId(), primaryInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        Module m = new Module("M1", 1);
        m.addLecture(new Lecture("L1", 1, new VideoContent("http://v.mp4", 0))); // 0 seconds
        course.addModule(m);

        CourseValidationException ex = assertThrows(CourseValidationException.class, () -> pipeline.validate(course));
        assertTrue(ex.getMessage().contains("video duration greater than 0 seconds"));
    }

    @Test
    @DisplayName("Pipeline Success: All chain handlers pass when requirements are fulfilled")
    void testPipelinePassesSuccessfully() {
        course.addAuthorship(new CourseAuthorship(course.getId(), primaryInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 80.0));
        course.addAuthorship(new CourseAuthorship(course.getId(), coInstructor, AuthorshipRole.CO_INSTRUCTOR, 20.0));

        Module m = new Module("M1", 1);
        m.addLecture(new Lecture("L1", 1, new VideoContent("http://v.mp4", 600)));
        course.addModule(m);

        assertDoesNotThrow(() -> pipeline.validate(course));
    }
}

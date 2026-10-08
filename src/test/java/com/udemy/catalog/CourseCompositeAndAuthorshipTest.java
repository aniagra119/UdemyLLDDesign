package com.udemy.catalog;

import com.udemy.enums.AuthorshipRole;
import com.udemy.enums.CourseStatus;
import com.udemy.enums.ResourceType;
import com.udemy.media.FileAttachment;
import com.udemy.media.ReadingLink;
import com.udemy.media.ResourceFactory;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.strategy.PromotionalTierPricingStrategy;
import com.udemy.strategy.StandardPricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course Composite & Multi-Authorship Architecture Tests")
class CourseCompositeAndAuthorshipTest {

    private Instructor leadInstructor;
    private Instructor coInstructor;
    private Course course;

    @BeforeEach
    void setUp() {
        leadInstructor = new Instructor("lead@tech.com", "Anirudh Agrawal", "Distributed Systems & LLD");
        coInstructor = new Instructor("co@tech.com", "Sundar P.", "Cloud Architecture");
        course = new Course("Mastering Distributed Systems & LLD", "Comprehensive guide to Staff-level architecture", 4999.0);
    }

    @Test
    @DisplayName("Composite Pattern: Duration and lecture count correctly roll up from lectures to modules to course")
    void testCompositeDurationAndLectureCountRollup() {
        // Module 1: Concurrency (2 lectures: 600s + 900s = 1500s)
        Module module1 = new Module("Module 1: Concurrency Deep-Dive", 1);
        Lecture lecture1 = new Lecture("Lec 1.1: Thread Safety & Locks", 1, new VideoContent("http://cdn/lec1.mp4", 600));
        Lecture lecture2 = new Lecture("Lec 1.2: Lock-Free Queues", 2, new VideoContent("http://cdn/lec2.mp4", 900));
        module1.addLecture(lecture1);
        module1.addLecture(lecture2);

        // Module 2: System Design (1 lecture: 1800s)
        Module module2 = new Module("Module 2: Distributed Consensus", 2);
        Lecture lecture3 = new Lecture("Lec 2.1: Raft Consensus Protocol", 1, new VideoContent("http://cdn/lec3.mp4", 1800));
        module2.addLecture(lecture3);

        course.addModule(module1);
        course.addModule(module2);

        assertEquals(2, module1.getLectureCount());
        assertEquals(1500, module1.getDurationSeconds());

        assertEquals(1, module2.getLectureCount());
        assertEquals(1800, module2.getDurationSeconds());

        // Entire course composite roll-up
        assertEquals(3, course.getLectureCount());
        assertEquals(3300, course.getDurationSeconds()); // 1500 + 1800 = 3300s (55 mins)
    }

    @Test
    @DisplayName("Multi-Authorship: A course can have multiple instructors with revenue share splits")
    void testMultiInstructorPublishing() {
        CourseAuthorship leadAuthorship = new CourseAuthorship(course.getId(), leadInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 70.0);
        CourseAuthorship coAuthorship = new CourseAuthorship(course.getId(), coInstructor, AuthorshipRole.CO_INSTRUCTOR, 30.0);

        course.addAuthorship(leadAuthorship);
        course.addAuthorship(coAuthorship);

        assertEquals(2, course.getInstructors().size());
        assertTrue(course.getInstructors().contains(leadInstructor));
        assertTrue(course.getInstructors().contains(coInstructor));

        assertEquals(1, leadInstructor.getAuthorships().size());
        assertEquals(1, coInstructor.getAuthorships().size());
        assertEquals(70.0, leadAuthorship.getRevenueSharePercentage());
        assertEquals(30.0, coAuthorship.getRevenueSharePercentage());
    }

    @Test
    @DisplayName("Publishing Invariants: Cannot publish course without lectures or without instructors")
    void testCoursePublishingInvariants() {
        // Empty course cannot be published
        assertThrows(IllegalStateException.class, () -> course.publish());

        // Add module with lecture
        Module mod = new Module("Module 1", 1);
        mod.addLecture(new Lecture("Lec 1", 1, new VideoContent("http://cdn/v.mp4", 300)));
        course.addModule(mod);

        // Still cannot publish without instructor
        assertThrows(IllegalStateException.class, () -> course.publish());

        // Add instructor -> publish succeeds
        course.addAuthorship(new CourseAuthorship(course.getId(), leadInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));
        course.publish();

        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
        assertTrue(course.isPublished());
    }

    @Test
    @DisplayName("Media Resources: Lectures support video content, file attachments, and reading links")
    void testLectureMediaAndResources() {
        VideoContent video = new VideoContent("http://cdn/video.mp4", 1200, "http://cdn/thumb.jpg");
        video.addStreamingResolution("1080p", "http://cdn/1080p.m3u8");
        video.addStreamingResolution("720p", "http://cdn/720p.m3u8");

        Lecture lecture = new Lecture("Distributed Locking", 1, video);

        FileAttachment pdf = ResourceFactory.createFileAttachment("Cheat Sheet", "http://cdn/locking.pdf", 204800, "pdf");
        ReadingLink link = ResourceFactory.createReadingLink("Martin Kleppmann Blog", "https://martin.kleppmann.com", "Redlock analysis");

        lecture.addResource(pdf);
        lecture.addResource(link);

        assertEquals(2, lecture.getResources().size());
        assertEquals(ResourceType.FILE_ATTACHMENT, pdf.getResourceType());
        assertEquals(ResourceType.READING_LINK, link.getResourceType());
        assertEquals(2, video.getStreamingResolutions().size());
    }

    @Test
    @DisplayName("Pricing Strategies: Standard vs Promotional Tier pricing calculation")
    void testPricingStrategies() {
        Student student = new Student("student@learn.com", "Rahul Sharma");
        StandardPricingStrategy standard = new StandardPricingStrategy();
        assertEquals(4999.0, standard.calculatePrice(course, student));

        PromotionalTierPricingStrategy promo = new PromotionalTierPricingStrategy(20.0); // 20% off
        assertEquals(3999.2, promo.calculatePrice(course, student));
    }
}

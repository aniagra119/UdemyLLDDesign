package com.udemy.model;

import com.udemy.enums.AuthorshipRole;
import com.udemy.enums.CourseStatus;
import com.udemy.enums.ResourceType;
import com.udemy.media.FileAttachment;
import com.udemy.media.ReadingLink;
import com.udemy.media.ResourceFactory;
import com.udemy.media.VideoContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Entity & Invariant Tests")
class DomainEntityTest {

    @Test
    @DisplayName("User Hierarchy: Instructor and Student inherit BaseEntity id and timestamps")
    void testUserHierarchy() {
        Instructor instructor = new Instructor("inst@tech.com", "John Doe", "Systems");
        assertNotNull(instructor.getId());
        assertNotNull(instructor.getCreatedAt());
        assertEquals("John Doe", instructor.getFullName());
        assertEquals("Systems", instructor.getExpertiseArea());

        instructor.setBiography("Senior Engineer with 10 years experience");
        assertEquals("Senior Engineer with 10 years experience", instructor.getBiography());

        Student student = new Student("stud@tech.com", "Jane Doe");
        assertEquals("stud@tech.com", student.getEmail());
        assertFalse(student.isEnrolled("any-course"));
    }

    @Test
    @DisplayName("CourseAuthorship: Validates revenue share percentage between 0 and 100")
    void testCourseAuthorshipValidation() {
        Instructor inst = new Instructor("inst@tech.com", "Author", "AI");
        CourseAuthorship authorship = new CourseAuthorship("c1", inst, AuthorshipRole.LEAD_INSTRUCTOR, 60.0);
        assertEquals(60.0, authorship.getRevenueSharePercentage());

        assertThrows(IllegalArgumentException.class, () -> authorship.setRevenueSharePercentage(-5.0));
        assertThrows(IllegalArgumentException.class, () -> authorship.setRevenueSharePercentage(105.0));
    }

    @Test
    @DisplayName("Course Invariants: Base price cannot be negative")
    void testCoursePriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Course("Title", "Desc", -10.0));
    }

    @Test
    @DisplayName("VideoContent Invariants: Duration cannot be negative")
    void testVideoDurationValidation() {
        assertThrows(IllegalArgumentException.class, () -> new VideoContent("http://cdn/v.mp4", -1));
    }

    @Test
    @DisplayName("FileAttachment Invariants: File size cannot be negative")
    void testFileAttachmentValidation() {
        assertThrows(IllegalArgumentException.class, () -> ResourceFactory.createFileAttachment("Cheat Sheet", "http://cdn/f.pdf", -10, "pdf"));
    }

    @Test
    @DisplayName("Course Lifecycle: Transitions from DRAFT to PUBLISHED and ARCHIVED")
    void testCourseStatusTransitions() {
        Course course = new Course("Lifecycle Course", "Testing transitions", 1999.0);
        assertEquals(CourseStatus.DRAFT, course.getStatus());

        Instructor inst = new Instructor("i@t.com", "Inst", "Topic");
        course.addAuthorship(new CourseAuthorship(course.getId(), inst, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));
        Module mod = new Module("M1", 1);
        mod.addLecture(new Lecture("L1", 1, new VideoContent("http://v.mp4", 100)));
        course.addModule(mod);

        course.publish();
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());

        course.archive();
        assertEquals(CourseStatus.ARCHIVED, course.getStatus());
        assertFalse(course.isPublished());
    }
}

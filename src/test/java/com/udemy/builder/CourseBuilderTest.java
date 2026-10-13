package com.udemy.builder;

import com.udemy.adapter.cdn.AwsCloudFrontAdapter;
import com.udemy.enums.AuthorshipRole;
import com.udemy.enums.CourseStatus;
import com.udemy.model.Course;
import com.udemy.model.Instructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Builder Pattern: Fluent Course & Curriculum Construction Tests")
class CourseBuilderTest {

    @Test
    @DisplayName("Fluent Builder: Assembles complete multi-tier course with instructors, modules, lectures, and resources")
    void testFluentCourseBuilder() {
        Instructor lead = new Instructor("lead@tech.com", "Anirudh Agrawal", "Senior Staff Systems");
        Instructor co = new Instructor("co@tech.com", "Martin F.", "Refactoring");

        Course course = CourseBuilder.create("Enterprise Reactive Systems", "Production Spring Boot 3 & Loom", 5499.0)
                .withLeadInstructor(lead, 75.0)
                .withCoInstructor(co, 25.0)
                .withCdnAdapter(new AwsCloudFrontAdapter("d123.cloudfront.net", "K12345"))
                .addModule("Module 1: Concurrency Internals", m -> m
                        .addLecture("Lec 1.1: Virtual Threads", 1800, "videos/vt.mp4", l -> l
                                .addFileAttachment("Loom Cheat Sheet", "files/loom.pdf", 102400, "pdf")
                                .addReadingLink("JEP 444", "https://openjdk.org/jeps/444", "Official Spec")
                        )
                        .addLecture("Lec 1.2: Structured Concurrency", 1200, "videos/structured.mp4")
                )
                .addModule("Module 2: Reactive Streams", m -> m
                        .addLecture("Lec 2.1: Backpressure", 1500, "videos/backpressure.mp4")
                )
                .build();

        assertNotNull(course);
        assertEquals("Enterprise Reactive Systems", course.getTitle());
        assertEquals(5499.0, course.getBasePrice());
        assertEquals(CourseStatus.DRAFT, course.getStatus());

        // Validate multi-authorship
        assertEquals(2, course.getInstructors().size());
        assertEquals(75.0, course.getAuthorships().get(0).getRevenueSharePercentage());
        assertEquals(25.0, course.getAuthorships().get(1).getRevenueSharePercentage());

        // Validate curriculum composite rollup
        assertEquals(2, course.getModules().size());
        assertEquals(3, course.getLectureCount());
        assertEquals(4500, course.getDurationSeconds()); // 1800 + 1200 + 1500 = 4500s

        // Validate resources on lecture 1.1
        assertEquals(2, course.getModules().get(0).getLectures().get(0).getResources().size());

        // Validate CDN adapter
        assertNotNull(course.getCdnAdapter());
        assertEquals("AWS_CLOUDFRONT", course.getCdnAdapter().getProviderName());
    }
}

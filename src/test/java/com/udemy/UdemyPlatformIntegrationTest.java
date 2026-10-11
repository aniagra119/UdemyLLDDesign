package com.udemy;

import com.udemy.enums.AuthorshipRole;
import com.udemy.event.CertificateGeneratorListener;
import com.udemy.media.ResourceFactory;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.progress.Enrollment;
import com.udemy.progress.PlaybackResumeSnapshot;
import com.udemy.service.UdemyLearningPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Udemy Platform Full End-to-End Integration Test")
class UdemyPlatformIntegrationTest {

    private UdemyLearningPlatform platform;
    private CertificateGeneratorListener certificateListener;
    private Instructor primaryInstructor;
    private Instructor coInstructor;
    private Course course;
    private Lecture lec1;
    private Lecture lec2;

    @BeforeEach
    void setUp() {
        platform = new UdemyLearningPlatform();
        certificateListener = new CertificateGeneratorListener();
        platform.registerEventListener(certificateListener);

        primaryInstructor = new Instructor("anirudh@tech.com", "Anirudh Agrawal", "Senior Staff Systems");
        coInstructor = new Instructor("linus@os.com", "Linus T.", "Kernel Architecture");

        course = new Course("High-Throughput Reactive Microservices", "Spring Boot 3, Kafka, and Virtual Threads", 5999.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), primaryInstructor, AuthorshipRole.LEAD_INSTRUCTOR, 80.0));
        course.addAuthorship(new CourseAuthorship(course.getId(), coInstructor, AuthorshipRole.CO_INSTRUCTOR, 20.0));

        Module m1 = new Module("Virtual Threads & Loom", 1);
        lec1 = new Lecture("Lec 1: Carrier Threads & Continuations", 1, new VideoContent("http://cdn/vt.mp4", 1800)); // 30 mins
        lec1.addResource(ResourceFactory.createFileAttachment("Loom Spec", "http://cdn/loom.pdf", 512000, "pdf"));

        lec2 = new Lecture("Lec 2: Structured Concurrency Scope", 2, new VideoContent("http://cdn/structured.mp4", 1200)); // 20 mins
        lec2.addResource(ResourceFactory.createReadingLink("JEP 453", "https://openjdk.org/jeps/453", "Preview Spec"));

        m1.addLecture(lec1);
        m1.addLecture(lec2);
        course.addModule(m1);

        platform.registerCourse(course);
        platform.publishCourse(course.getId());
    }

    @Test
    @DisplayName("Complete Learner Flow: Multi-instructor publishing -> Enrollment -> Resumable drop-off at 10m -> Completion -> Certificate -> 5-star Rating")
    void testCompleteLearnerLifecycle() {
        // 1. Student registers and enrolls
        Student student = new Student("vikram@enterprise.com", "Vikram Malhotra");
        Enrollment enrollment = platform.enroll(student, course.getId());
        assertNotNull(enrollment);
        assertEquals(1, student.getEnrolledCourseIds().size());

        // 2. Student starts Lecture 1 (30 mins = 1800s) and watches 10 mins (600s), then drops off
        platform.recordPlaybackHeartbeat(student, course.getId(), lec1.getId(), 600);

        // 3. Next day, student logs back in and hits "Resume"
        Optional<PlaybackResumeSnapshot> resumeOpt = platform.resumePlayback(student, course.getId());
        assertTrue(resumeOpt.isPresent());
        PlaybackResumeSnapshot snapshot = resumeOpt.get();
        assertEquals(lec1.getId(), snapshot.getLectureId());
        assertEquals(600, snapshot.getResumePositionSeconds(), "Resume position must be exactly 10 minutes (600 seconds)");
        assertEquals("10:00 / 30:00", snapshot.getFormattedResumeTime());

        // 4. Student finishes lecture 1 (watched 1700s out of 1800s -> >90% -> auto completed)
        platform.recordPlaybackHeartbeat(student, course.getId(), lec1.getId(), 1700);

        // 5. Student watches and finishes lecture 2 (watched 1150s out of 1200s -> >90% -> completed)
        platform.recordPlaybackHeartbeat(student, course.getId(), lec2.getId(), 1150);

        // 6. Verify course completion and asynchronous Certificate generation
        assertTrue(enrollment.isCompleted());
        assertTrue(certificateListener.hasCertificate(student.getId(), course.getId()),
                "Certificate should be automatically generated upon course completion");

        // 7. Verified student rates the course 5 stars
        platform.rateCourse(student, course.getId(), 5.0, "The best distributed systems course on the internet!");
        assertEquals(5.0, course.getAverageRating());
        assertEquals(1, course.getTotalRatingsCount());
    }
}

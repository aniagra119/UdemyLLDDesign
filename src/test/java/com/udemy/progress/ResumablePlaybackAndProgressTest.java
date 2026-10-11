package com.udemy.progress;

import com.udemy.enums.AuthorshipRole;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.service.PlaybackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Resumable Video Playback & Granular Progress Tracking Tests")
class ResumablePlaybackAndProgressTest {

    private Student student;
    private Course course;
    private Module module1;
    private Lecture lecture1;
    private Lecture lecture2;
    private PlaybackService playbackService;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = new Student("alice@learner.com", "Alice Smith");
        Instructor instructor = new Instructor("dan@pro.com", "Dan Abramov", "Frontend Architecture");
        course = new Course("Modern React Internals", "Deep dive into Fiber and reconciliation", 3499.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        module1 = new Module("Module 1: The Fiber Tree", 1);
        // Lecture 1: 30 minutes long (1800 seconds)
        lecture1 = new Lecture("Lec 1.1: WorkLoop & Fiber Nodes", 1, new VideoContent("http://cdn/fiber.mp4", 1800));
        // Lecture 2: 20 minutes long (1200 seconds)
        lecture2 = new Lecture("Lec 1.2: Concurrent Mode Scheduler", 2, new VideoContent("http://cdn/scheduler.mp4", 1200));

        module1.addLecture(lecture1);
        module1.addLecture(lecture2);
        course.addModule(module1);
        course.publish();

        enrollment = new Enrollment(student, course);
        playbackService = new PlaybackService();
    }

    @Test
    @DisplayName("Requirements Scenario: Resumes video from 10:00 (600s) mark when user dropped off after watching 10 mins")
    void testResumePlaybackFromDropOffMark() {
        // User starts lecture 1 (30 mins = 1800s) and watches until 10 mins (600 seconds)
        playbackService.recordHeartbeat(enrollment, lecture1.getId(), 600);

        // User drops off and logs back in -> queries resume snapshot
        Optional<PlaybackResumeSnapshot> resumeOpt = playbackService.resumePlayback(enrollment);

        assertTrue(resumeOpt.isPresent(), "Resume snapshot must be present");
        PlaybackResumeSnapshot snapshot = resumeOpt.get();

        assertEquals(lecture1.getId(), snapshot.getLectureId());
        assertEquals("Lec 1.1: WorkLoop & Fiber Nodes", snapshot.getLectureTitle());
        assertEquals(600, snapshot.getResumePositionSeconds(), "Resume position must be exactly 600 seconds (10 mins)");
        assertEquals(1800, snapshot.getTotalDurationSeconds());
        assertEquals("10:00 / 30:00", snapshot.getFormattedResumeTime());
    }

    @Test
    @DisplayName("Auto-Completion: Lecture automatically marks completed when watched >= 90% threshold")
    void testAutoCompletionAtNinetyPercentThreshold() {
        // Watch 85% of lecture 1 (1530s out of 1800s) -> not completed yet
        playbackService.recordHeartbeat(enrollment, lecture1.getId(), 1530);
        assertFalse(enrollment.getProgress().getOrCreateLectureProgress(lecture1.getId()).isCompleted());
        assertEquals(0, enrollment.getProgress().getCompletedLectureCount());

        // Watch 90% (1620s out of 1800s) -> automatically marks completed!
        playbackService.recordHeartbeat(enrollment, lecture1.getId(), 1620);
        assertTrue(enrollment.getProgress().getOrCreateLectureProgress(lecture1.getId()).isCompleted());
        assertEquals(1, enrollment.getProgress().getCompletedLectureCount());
        assertEquals(50.0, enrollment.getProgress().getCompletionPercentage(course.getLectureCount()));
    }

    @Test
    @DisplayName("Bounds Clamping: Playback position cannot exceed total video duration")
    void testPlaybackBoundsClamping() {
        // Attempting to record 2500s on an 1800s video clamps to 1800s
        playbackService.recordHeartbeat(enrollment, lecture1.getId(), 2500);

        LectureProgress lp = enrollment.getProgress().getOrCreateLectureProgress(lecture1.getId());
        assertEquals(1800, lp.getLastWatchedPositionSeconds());
        assertTrue(lp.isCompleted());
    }
}

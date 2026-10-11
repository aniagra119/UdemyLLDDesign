package com.udemy.concurrency;

import com.udemy.enums.AuthorshipRole;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.progress.Enrollment;
import com.udemy.service.PlaybackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrency Stress Tests: High-Frequency Playback Heartbeats")
class ConcurrentPlaybackHeartbeatTest {

    @Test
    @DisplayName("Stress Test: 20 concurrent heartbeat updates from web/mobile clients execute without race condition")
    void testConcurrentPlaybackHeartbeats() throws InterruptedException {
        Instructor instructor = new Instructor("lead@tech.com", "Anirudh Agrawal", "Concurrency");
        Course course = new Course("Concurrent Java Internals", "Race conditions & atomic locks", 2999.0);
        course.addAuthorship(new CourseAuthorship(course.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));

        Module m1 = new Module("Concurrency Core", 1);
        Lecture lecture = new Lecture("CAS Loops", 1, new VideoContent("http://cdn/cas.mp4", 3600)); // 1 hour video
        m1.addLecture(lecture);
        course.addModule(m1);
        course.publish();

        Student student = new Student("stress@user.com", "Stress Tester");
        Enrollment enrollment = new Enrollment(student, course);
        PlaybackService playbackService = new PlaybackService();

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger successfulHeartbeats = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final int timestamp = i * 60; // 60s, 120s, 180s... up to 1200s
            executor.submit(() -> {
                try {
                    startLatch.await(); // Thundering herd: all threads start simultaneously
                    playbackService.recordHeartbeat(enrollment, lecture.getId(), timestamp);
                    successfulHeartbeats.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Release threads
        boolean finished = finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(finished, "All threads should complete within 5 seconds");
        assertEquals(threadCount, successfulHeartbeats.get(), "All 20 heartbeats must be recorded");

        // The maxWatchedPositionSeconds should reflect the highest timestamp (20 * 60 = 1200s)
        int maxWatched = enrollment.getProgress().getOrCreateLectureProgress(lecture.getId()).getMaxWatchedPositionSeconds();
        assertEquals(1200, maxWatched, "Max watched timestamp must be 1200 seconds");
    }
}

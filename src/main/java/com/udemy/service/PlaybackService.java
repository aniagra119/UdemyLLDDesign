package com.udemy.service;

import com.udemy.exception.InvalidPlaybackPositionException;
import com.udemy.exception.ResourceNotFoundException;
import com.udemy.model.Course;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.progress.Enrollment;
import com.udemy.progress.PlaybackResumeSnapshot;

import java.util.Objects;
import java.util.Optional;

public class PlaybackService {

    public synchronized void recordHeartbeat(Enrollment enrollment, String lectureId, int currentTimestampSeconds) {
        Objects.requireNonNull(enrollment, "enrollment must not be null");
        Objects.requireNonNull(lectureId, "lectureId must not be null");

        Course course = enrollment.getCourse();
        Lecture lecture = findLecture(course, lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));

        int duration = lecture.getDurationSeconds();
        if (currentTimestampSeconds < 0) {
            throw new InvalidPlaybackPositionException("Playback timestamp cannot be negative: " + currentTimestampSeconds);
        }

        enrollment.recordPlayback(lectureId, currentTimestampSeconds, duration);
    }

    public Optional<PlaybackResumeSnapshot> resumePlayback(Enrollment enrollment) {
        Objects.requireNonNull(enrollment, "enrollment must not be null");
        return enrollment.resumePlayback();
    }

    private Optional<Lecture> findLecture(Course course, String lectureId) {
        for (Module m : course.getModules()) {
            for (Lecture l : m.getLectures()) {
                if (l.getId().equals(lectureId)) {
                    return Optional.of(l);
                }
            }
        }
        return Optional.empty();
    }
}

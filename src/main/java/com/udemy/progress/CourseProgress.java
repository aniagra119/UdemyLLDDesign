package com.udemy.progress;

import com.udemy.model.Course;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.strategy.CompletionCriteriaStrategy;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CourseProgress {
    private final String enrollmentId;
    private volatile String lastAccessedLectureId;
    private final Map<String, LectureProgress> lectureProgressMap;

    public CourseProgress(String enrollmentId) {
        this.enrollmentId = Objects.requireNonNull(enrollmentId, "enrollmentId must not be null");
        this.lectureProgressMap = new ConcurrentHashMap<>();
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getLastAccessedLectureId() {
        return lastAccessedLectureId;
    }

    public LectureProgress getOrCreateLectureProgress(String lectureId) {
        return lectureProgressMap.computeIfAbsent(lectureId, LectureProgress::new);
    }

    public void recordPlayback(String lectureId, int currentSeconds, int totalDurationSeconds, CompletionCriteriaStrategy strategy) {
        LectureProgress lp = getOrCreateLectureProgress(lectureId);
        lp.updatePosition(currentSeconds, totalDurationSeconds, strategy);
        this.lastAccessedLectureId = lectureId;
    }

    public int getCompletedLectureCount() {
        return (int) lectureProgressMap.values().stream().filter(LectureProgress::isCompleted).count();
    }

    public double getCompletionPercentage(int totalLecturesInCourse) {
        if (totalLecturesInCourse <= 0) return 0.0;
        int completed = getCompletedLectureCount();
        double pct = ((double) completed / totalLecturesInCourse) * 100.0;
        return Math.min(100.0, Math.round(pct * 10.0) / 10.0);
    }

    public boolean isCourseCompleted(int totalLecturesInCourse) {
        return totalLecturesInCourse > 0 && getCompletedLectureCount() >= totalLecturesInCourse;
    }

    public Optional<PlaybackResumeSnapshot> getResumeSnapshot(Course course) {
        if (course == null || course.getModules().isEmpty()) {
            return Optional.empty();
        }

        // If user already accessed a lecture previously, resume from there
        if (lastAccessedLectureId != null) {
            for (Module m : course.getModules()) {
                for (Lecture l : m.getLectures()) {
                    if (l.getId().equals(lastAccessedLectureId)) {
                        LectureProgress lp = getOrCreateLectureProgress(l.getId());
                        return Optional.of(new PlaybackResumeSnapshot(
                                course.getId(),
                                m.getId(),
                                l.getId(),
                                l.getTitle(),
                                lp.getLastWatchedPositionSeconds(),
                                l.getDurationSeconds()
                        ));
                    }
                }
            }
        }

        // Otherwise find first uncompleted lecture, or first lecture of first module
        for (Module m : course.getModules()) {
            for (Lecture l : m.getLectures()) {
                LectureProgress lp = lectureProgressMap.get(l.getId());
                if (lp == null || !lp.isCompleted()) {
                    int resumeSec = lp != null ? lp.getLastWatchedPositionSeconds() : 0;
                    return Optional.of(new PlaybackResumeSnapshot(
                            course.getId(),
                            m.getId(),
                            l.getId(),
                            l.getTitle(),
                            resumeSec,
                            l.getDurationSeconds()
                    ));
                }
            }
        }

        // If all completed, return first lecture of first module at offset 0
        Module firstModule = course.getModules().getFirst();
        if (!firstModule.getLectures().isEmpty()) {
            Lecture firstLec = firstModule.getLectures().getFirst();
            return Optional.of(new PlaybackResumeSnapshot(
                    course.getId(),
                    firstModule.getId(),
                    firstLec.getId(),
                    firstLec.getTitle(),
                    0,
                    firstLec.getDurationSeconds()
            ));
        }

        return Optional.empty();
    }
}

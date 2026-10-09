package com.udemy.progress;

import com.udemy.enums.EnrollmentStatus;
import com.udemy.model.BaseEntity;
import com.udemy.model.Course;
import com.udemy.model.Student;
import com.udemy.strategy.CompletionCriteriaStrategy;
import com.udemy.strategy.PercentageThresholdCompletionStrategy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Enrollment extends BaseEntity {
    private final Student student;
    private final Course course;
    private EnrollmentStatus status;
    private final LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private final CourseProgress progress;
    private CompletionCriteriaStrategy completionStrategy;

    public Enrollment(Student student, Course course) {
        this(student, course, new PercentageThresholdCompletionStrategy(90.0));
    }

    public Enrollment(Student student, Course course, CompletionCriteriaStrategy completionStrategy) {
        super();
        this.student = Objects.requireNonNull(student, "student must not be null");
        this.course = Objects.requireNonNull(course, "course must not be null");
        this.status = EnrollmentStatus.ACTIVE;
        this.enrolledAt = LocalDateTime.now();
        this.progress = new CourseProgress(this.id);
        this.completionStrategy = completionStrategy != null ? completionStrategy : new PercentageThresholdCompletionStrategy(90.0);
        this.student.addEnrolledCourse(course.getId());
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public CourseProgress getProgress() {
        return progress;
    }

    public void setCompletionStrategy(CompletionCriteriaStrategy completionStrategy) {
        this.completionStrategy = completionStrategy;
    }

    public void recordPlayback(String lectureId, int currentSeconds, int totalDurationSeconds) {
        if (status != EnrollmentStatus.ACTIVE && status != EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update playback for inactive enrollment: " + status);
        }
        progress.recordPlayback(lectureId, currentSeconds, totalDurationSeconds, completionStrategy);

        if (status == EnrollmentStatus.ACTIVE && progress.isCourseCompleted(course.getLectureCount())) {
            this.status = EnrollmentStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
        }
        markUpdated();
    }

    public Optional<PlaybackResumeSnapshot> resumePlayback() {
        return progress.getResumeSnapshot(course);
    }

    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED || progress.isCourseCompleted(course.getLectureCount());
    }
}

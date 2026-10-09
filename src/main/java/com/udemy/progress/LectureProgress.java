package com.udemy.progress;

import com.udemy.strategy.CompletionCriteriaStrategy;
import java.time.LocalDateTime;
import java.util.Objects;

public class LectureProgress {
    private final String lectureId;
    private int lastWatchedPositionSeconds;
    private int maxWatchedPositionSeconds;
    private volatile boolean completed;
    private LocalDateTime lastAccessedAt;

    public LectureProgress(String lectureId) {
        this.lectureId = Objects.requireNonNull(lectureId, "lectureId must not be null");
        this.lastWatchedPositionSeconds = 0;
        this.maxWatchedPositionSeconds = 0;
        this.completed = false;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public synchronized void updatePosition(int currentSeconds, int totalDurationSeconds, CompletionCriteriaStrategy strategy) {
        int boundedSeconds = Math.min(Math.max(0, currentSeconds), totalDurationSeconds);
        this.lastWatchedPositionSeconds = boundedSeconds;
        this.maxWatchedPositionSeconds = Math.max(this.maxWatchedPositionSeconds, boundedSeconds);
        this.lastAccessedAt = LocalDateTime.now();

        if (!this.completed && strategy != null && strategy.isLectureCompleted(lastWatchedPositionSeconds, maxWatchedPositionSeconds, totalDurationSeconds)) {
            this.completed = true;
        }
    }

    public synchronized void markCompleted() {
        this.completed = true;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public String getLectureId() {
        return lectureId;
    }

    public synchronized int getLastWatchedPositionSeconds() {
        return lastWatchedPositionSeconds;
    }

    public synchronized int getMaxWatchedPositionSeconds() {
        return maxWatchedPositionSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public synchronized LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }
}

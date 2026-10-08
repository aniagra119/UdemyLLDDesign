package com.udemy.strategy;

public interface CompletionCriteriaStrategy {
    boolean isLectureCompleted(int lastWatchedSeconds, int maxWatchedSeconds, int totalDurationSeconds);
}

package com.udemy.strategy;

public class PercentageThresholdCompletionStrategy implements CompletionCriteriaStrategy {
    private final double thresholdPercentage; // e.g. 90.0

    public PercentageThresholdCompletionStrategy() {
        this(90.0);
    }

    public PercentageThresholdCompletionStrategy(double thresholdPercentage) {
        if (thresholdPercentage < 0.0 || thresholdPercentage > 100.0) {
            throw new IllegalArgumentException("Threshold percentage must be between 0 and 100, got: " + thresholdPercentage);
        }
        this.thresholdPercentage = thresholdPercentage;
    }

    public double getThresholdPercentage() {
        return thresholdPercentage;
    }

    @Override
    public boolean isLectureCompleted(int lastWatchedSeconds, int maxWatchedSeconds, int totalDurationSeconds) {
        if (totalDurationSeconds <= 0) return true;
        double ratio = (double) maxWatchedSeconds / totalDurationSeconds;
        return ratio >= (thresholdPercentage / 100.0);
    }
}

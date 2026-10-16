package com.udemy.command;

import com.udemy.progress.Enrollment;

import java.util.Objects;

public class SeekPlaybackCommand implements PlaybackCommand {
    private final Enrollment enrollment;
    private final String lectureId;
    private final int targetSeconds;
    private final int totalDurationSeconds;
    private int previousSeconds;

    public SeekPlaybackCommand(Enrollment enrollment, String lectureId, int targetSeconds, int totalDurationSeconds) {
        this.enrollment = Objects.requireNonNull(enrollment, "enrollment must not be null");
        this.lectureId = Objects.requireNonNull(lectureId, "lectureId must not be null");
        this.targetSeconds = targetSeconds;
        this.totalDurationSeconds = totalDurationSeconds;
    }

    @Override
    public void execute() {
        this.previousSeconds = enrollment.getProgress()
                .getOrCreateLectureProgress(lectureId)
                .getLastWatchedPositionSeconds();
        enrollment.recordPlayback(lectureId, targetSeconds, totalDurationSeconds);
    }

    @Override
    public void undo() {
        enrollment.recordPlayback(lectureId, previousSeconds, totalDurationSeconds);
    }

    @Override
    public String getDescription() {
        return "Seek lecture " + lectureId + " from " + previousSeconds + "s to " + targetSeconds + "s";
    }

    public int getPreviousSeconds() {
        return previousSeconds;
    }

    public int getTargetSeconds() {
        return targetSeconds;
    }
}

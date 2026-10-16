package com.udemy.command;

import com.udemy.progress.Enrollment;
import com.udemy.progress.LectureProgress;

import java.util.Objects;

public class ToggleLectureCompleteCommand implements PlaybackCommand {
    private final Enrollment enrollment;
    private final String lectureId;
    private boolean previousCompletedState;

    public ToggleLectureCompleteCommand(Enrollment enrollment, String lectureId) {
        this.enrollment = Objects.requireNonNull(enrollment, "enrollment must not be null");
        this.lectureId = Objects.requireNonNull(lectureId, "lectureId must not be null");
    }

    @Override
    public void execute() {
        LectureProgress lp = enrollment.getProgress().getOrCreateLectureProgress(lectureId);
        this.previousCompletedState = lp.isCompleted();
        lp.markCompleted();
    }

    @Override
    public void undo() {
        if (!previousCompletedState) {
            // Revert by recording position back
            LectureProgress lp = enrollment.getProgress().getOrCreateLectureProgress(lectureId);
            // Re-instantiate progress with previous state if needed
        }
    }

    @Override
    public String getDescription() {
        return "Mark lecture " + lectureId + " as completed";
    }
}

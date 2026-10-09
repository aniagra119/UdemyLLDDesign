package com.udemy.progress;

import java.util.Objects;

public class PlaybackResumeSnapshot {
    private final String courseId;
    private final String moduleId;
    private final String lectureId;
    private final String lectureTitle;
    private final int resumePositionSeconds;
    private final int totalDurationSeconds;

    public PlaybackResumeSnapshot(String courseId, String moduleId, String lectureId, String lectureTitle,
                                  int resumePositionSeconds, int totalDurationSeconds) {
        this.courseId = Objects.requireNonNull(courseId, "courseId must not be null");
        this.moduleId = Objects.requireNonNull(moduleId, "moduleId must not be null");
        this.lectureId = Objects.requireNonNull(lectureId, "lectureId must not be null");
        this.lectureTitle = Objects.requireNonNull(lectureTitle, "lectureTitle must not be null");
        this.resumePositionSeconds = resumePositionSeconds;
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public int getResumePositionSeconds() {
        return resumePositionSeconds;
    }

    public int getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public String getFormattedResumeTime() {
        return formatTime(resumePositionSeconds) + " / " + formatTime(totalDurationSeconds);
    }

    private static String formatTime(int totalSecs) {
        int minutes = totalSecs / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return "PlaybackResumeSnapshot{" +
                "courseId='" + courseId + '\'' +
                ", lectureId='" + lectureId + '\'' +
                ", lectureTitle='" + lectureTitle + '\'' +
                ", resume=" + getFormattedResumeTime() +
                '}';
    }
}

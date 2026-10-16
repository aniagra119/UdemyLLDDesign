package com.udemy.command;

import com.udemy.model.BaseEntity;

import java.util.Objects;

public class Bookmark extends BaseEntity {
    private final String lectureId;
    private final String studentId;
    private final int timestampSeconds;
    private final String noteText;

    public Bookmark(String lectureId, String studentId, int timestampSeconds, String noteText) {
        super();
        this.lectureId = Objects.requireNonNull(lectureId, "lectureId must not be null");
        this.studentId = Objects.requireNonNull(studentId, "studentId must not be null");
        this.timestampSeconds = Math.max(0, timestampSeconds);
        this.noteText = noteText != null ? noteText : "";
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getTimestampSeconds() {
        return timestampSeconds;
    }

    public String getNoteText() {
        return noteText;
    }

    public String getFormattedTimestamp() {
        int minutes = timestampSeconds / 60;
        int seconds = timestampSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

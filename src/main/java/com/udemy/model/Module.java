package com.udemy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Module extends BaseEntity implements CourseComponent {
    private String title;
    private int orderIndex;
    private final List<Lecture> lectures;

    public Module(String title, int orderIndex) {
        super();
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
        this.lectures = new ArrayList<>();
    }

    public Module(String id, String title, int orderIndex) {
        super(id);
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
        this.lectures = new ArrayList<>();
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        markUpdated();
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
        markUpdated();
    }

    public List<Lecture> getLectures() {
        return Collections.unmodifiableList(lectures);
    }

    public void addLecture(Lecture lecture) {
        if (lecture != null && !lectures.contains(lecture)) {
            lectures.add(lecture);
            markUpdated();
        }
    }

    @Override
    public int getDurationSeconds() {
        return lectures.stream().mapToInt(Lecture::getDurationSeconds).sum();
    }

    @Override
    public int getLectureCount() {
        return lectures.size();
    }
}

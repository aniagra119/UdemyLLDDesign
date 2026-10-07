package com.udemy.model;

import com.udemy.media.LectureResource;
import com.udemy.media.VideoContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Lecture extends BaseEntity implements CourseComponent {
    private String title;
    private int orderIndex;
    private VideoContent video;
    private final List<LectureResource> resources;

    public Lecture(String title, int orderIndex, VideoContent video) {
        super();
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
        this.video = Objects.requireNonNull(video, "video must not be null");
        this.resources = new ArrayList<>();
    }

    public Lecture(String id, String title, int orderIndex, VideoContent video) {
        super(id);
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
        this.video = Objects.requireNonNull(video, "video must not be null");
        this.resources = new ArrayList<>();
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

    public VideoContent getVideo() {
        return video;
    }

    public void setVideo(VideoContent video) {
        this.video = Objects.requireNonNull(video, "video must not be null");
        markUpdated();
    }

    public List<LectureResource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    public void addResource(LectureResource resource) {
        if (resource != null && !resources.contains(resource)) {
            resources.add(resource);
            markUpdated();
        }
    }

    @Override
    public int getDurationSeconds() {
        return video != null ? video.getDurationSeconds() : 0;
    }

    @Override
    public int getLectureCount() {
        return 1;
    }
}

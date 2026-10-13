package com.udemy.builder;

import com.udemy.media.FileAttachment;
import com.udemy.media.LectureResource;
import com.udemy.media.ReadingLink;
import com.udemy.media.ResourceFactory;
import com.udemy.media.VideoContent;
import com.udemy.model.Lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LectureBuilder {
    private final String title;
    private final int orderIndex;
    private final VideoContent video;
    private final List<LectureResource> resources = new ArrayList<>();

    public LectureBuilder(String title, int orderIndex, VideoContent video) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
        this.video = Objects.requireNonNull(video, "video must not be null");
    }

    public LectureBuilder addFileAttachment(String name, String url, long bytes, String extension) {
        resources.add(ResourceFactory.createFileAttachment(name, url, bytes, extension));
        return this;
    }

    public LectureBuilder addReadingLink(String name, String targetUrl, String description) {
        resources.add(ResourceFactory.createReadingLink(name, targetUrl, description));
        return this;
    }

    public Lecture build() {
        Lecture lecture = new Lecture(title, orderIndex, video);
        for (LectureResource res : resources) {
            lecture.addResource(res);
        }
        return lecture;
    }
}

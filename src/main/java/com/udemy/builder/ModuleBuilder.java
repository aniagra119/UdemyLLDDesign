package com.udemy.builder;

import com.udemy.media.VideoContent;
import com.udemy.model.Lecture;
import com.udemy.model.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ModuleBuilder {
    private final String title;
    private final int orderIndex;
    private final List<Lecture> lectures = new ArrayList<>();
    private int currentLectureIndex = 1;

    public ModuleBuilder(String title, int orderIndex) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.orderIndex = orderIndex;
    }

    public ModuleBuilder addLecture(String title, int durationSeconds, String videoUrl) {
        VideoContent video = new VideoContent(videoUrl, durationSeconds);
        lectures.add(new Lecture(title, currentLectureIndex++, video));
        return this;
    }

    public ModuleBuilder addLecture(String title, int durationSeconds, String videoUrl, Consumer<LectureBuilder> configurator) {
        VideoContent video = new VideoContent(videoUrl, durationSeconds);
        LectureBuilder lb = new LectureBuilder(title, currentLectureIndex++, video);
        if (configurator != null) {
            configurator.accept(lb);
        }
        lectures.add(lb.build());
        return this;
    }

    public Module build() {
        Module module = new Module(title, orderIndex);
        for (Lecture lec : lectures) {
            module.addLecture(lec);
        }
        return module;
    }
}

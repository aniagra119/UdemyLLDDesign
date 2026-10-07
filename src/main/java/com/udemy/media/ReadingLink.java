package com.udemy.media;

import com.udemy.enums.ResourceType;
import java.util.Objects;

public class ReadingLink extends LectureResource {
    private final String targetUrl;
    private final String description;

    public ReadingLink(String title, String targetUrl, String description) {
        super(title, ResourceType.READING_LINK);
        this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must not be null");
        this.description = description != null ? description : "";
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getDescription() {
        return description;
    }
}

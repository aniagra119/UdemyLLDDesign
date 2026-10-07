package com.udemy.media;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VideoContent {
    private final String videoUrl;
    private final int durationSeconds;
    private String previewThumbnailUrl;
    private final Map<String, String> streamingResolutions; // e.g. "1080p" -> "url", "720p" -> "url"

    public VideoContent(String videoUrl, int durationSeconds) {
        this(videoUrl, durationSeconds, null);
    }

    public VideoContent(String videoUrl, int durationSeconds, String previewThumbnailUrl) {
        this.videoUrl = Objects.requireNonNull(videoUrl, "videoUrl must not be null");
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("durationSeconds cannot be negative: " + durationSeconds);
        }
        this.durationSeconds = durationSeconds;
        this.previewThumbnailUrl = previewThumbnailUrl;
        this.streamingResolutions = new HashMap<>();
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getPreviewThumbnailUrl() {
        return previewThumbnailUrl;
    }

    public void setPreviewThumbnailUrl(String previewThumbnailUrl) {
        this.previewThumbnailUrl = previewThumbnailUrl;
    }

    public Map<String, String> getStreamingResolutions() {
        return Collections.unmodifiableMap(streamingResolutions);
    }

    public void addStreamingResolution(String resolution, String streamUrl) {
        this.streamingResolutions.put(resolution, streamUrl);
    }
}

package com.udemy.media;

import com.udemy.enums.ResourceType;
import java.util.Objects;

public class FileAttachment extends LectureResource {
    private final String fileUrl;
    private final long fileSizeBytes;
    private final String fileExtension; // e.g. "pdf", "zip"

    public FileAttachment(String title, String fileUrl, long fileSizeBytes, String fileExtension) {
        super(title, ResourceType.FILE_ATTACHMENT);
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
        if (fileSizeBytes < 0) {
            throw new IllegalArgumentException("fileSizeBytes cannot be negative: " + fileSizeBytes);
        }
        this.fileSizeBytes = fileSizeBytes;
        this.fileExtension = Objects.requireNonNull(fileExtension, "fileExtension must not be null");
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}

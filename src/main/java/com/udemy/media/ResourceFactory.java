package com.udemy.media;

public class ResourceFactory {

    public static FileAttachment createFileAttachment(String title, String fileUrl, long sizeBytes, String extension) {
        return new FileAttachment(title, fileUrl, sizeBytes, extension);
    }

    public static ReadingLink createReadingLink(String title, String targetUrl, String description) {
        return new ReadingLink(title, targetUrl, description);
    }
}

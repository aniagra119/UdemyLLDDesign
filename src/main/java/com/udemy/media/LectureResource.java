package com.udemy.media;

import com.udemy.enums.ResourceType;
import com.udemy.model.BaseEntity;
import java.util.Objects;

public abstract class LectureResource extends BaseEntity {
    protected final String title;
    protected final ResourceType resourceType;

    protected LectureResource(String title, ResourceType resourceType) {
        super();
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.resourceType = Objects.requireNonNull(resourceType, "resourceType must not be null");
    }

    protected LectureResource(String id, String title, ResourceType resourceType) {
        super(id);
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.resourceType = Objects.requireNonNull(resourceType, "resourceType must not be null");
    }

    public String getTitle() {
        return title;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}

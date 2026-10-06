package com.udemy.model;

import java.util.Objects;

public abstract class User extends BaseEntity {
    protected String email;
    protected String fullName;
    protected String profilePictureUrl;

    protected User(String id, String email, String fullName) {
        super(id);
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
    }

    protected User(String email, String fullName) {
        super();
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        markUpdated();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        markUpdated();
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        markUpdated();
    }
}

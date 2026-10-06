package com.udemy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Instructor extends User {
    private String biography;
    private String expertiseArea;
    private final List<CourseAuthorship> authorships;

    public Instructor(String id, String email, String fullName, String expertiseArea) {
        super(id, email, fullName);
        this.expertiseArea = expertiseArea;
        this.authorships = new ArrayList<>();
    }

    public Instructor(String email, String fullName, String expertiseArea) {
        super(email, fullName);
        this.expertiseArea = expertiseArea;
        this.authorships = new ArrayList<>();
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
        markUpdated();
    }

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public void setExpertiseArea(String expertiseArea) {
        this.expertiseArea = expertiseArea;
        markUpdated();
    }

    public List<CourseAuthorship> getAuthorships() {
        return Collections.unmodifiableList(authorships);
    }

    public void addAuthorship(CourseAuthorship authorship) {
        if (authorship != null && !authorships.contains(authorship)) {
            authorships.add(authorship);
            markUpdated();
        }
    }
}

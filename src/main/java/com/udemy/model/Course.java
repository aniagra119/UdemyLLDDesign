package com.udemy.model;

import com.udemy.enums.CourseStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Course extends BaseEntity implements CourseComponent {
    private String title;
    private String description;
    private double basePrice;
    private CourseStatus status;
    private final List<CourseAuthorship> authorships;
    private final List<Module> modules;
    private final List<Review> reviews;
    private double averageRating;
    private int totalRatingsCount;

    public Course(String title, String description, double basePrice) {
        super();
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        if (basePrice < 0) {
            throw new IllegalArgumentException("basePrice cannot be negative: " + basePrice);
        }
        this.basePrice = basePrice;
        this.status = CourseStatus.DRAFT;
        this.authorships = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
        this.totalRatingsCount = 0;
    }

    public Course(String id, String title, String description, double basePrice) {
        super(id);
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        if (basePrice < 0) {
            throw new IllegalArgumentException("basePrice cannot be negative: " + basePrice);
        }
        this.basePrice = basePrice;
        this.status = CourseStatus.DRAFT;
        this.authorships = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
        this.totalRatingsCount = 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        markUpdated();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        markUpdated();
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("basePrice cannot be negative: " + basePrice);
        }
        this.basePrice = basePrice;
        markUpdated();
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void publish() {
        if (modules.isEmpty() || getLectureCount() == 0) {
            throw new IllegalStateException("Cannot publish an empty course without lectures");
        }
        if (authorships.isEmpty()) {
            throw new IllegalStateException("Cannot publish a course without at least one instructor");
        }
        this.status = CourseStatus.PUBLISHED;
        markUpdated();
    }

    public void archive() {
        this.status = CourseStatus.ARCHIVED;
        markUpdated();
    }

    public boolean isPublished() {
        return this.status == CourseStatus.PUBLISHED;
    }

    public List<CourseAuthorship> getAuthorships() {
        return Collections.unmodifiableList(authorships);
    }

    public void addAuthorship(CourseAuthorship authorship) {
        if (authorship != null && !authorships.contains(authorship)) {
            authorships.add(authorship);
            authorship.getInstructor().addAuthorship(authorship);
            markUpdated();
        }
    }

    public List<Instructor> getInstructors() {
        return authorships.stream()
                .map(CourseAuthorship::getInstructor)
                .toList();
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public void addModule(Module module) {
        if (module != null && !modules.contains(module)) {
            modules.add(module);
            markUpdated();
        }
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public synchronized void addReview(Review review) {
        if (review != null) {
            // Replace if existing review from same student
            reviews.removeIf(r -> r.getStudent().getId().equals(review.getStudent().getId()));
            reviews.add(review);
            recalculateRating();
            markUpdated();
        }
    }

    private void recalculateRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
            this.totalRatingsCount = 0;
            return;
        }
        double sum = 0.0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        this.totalRatingsCount = reviews.size();
        this.averageRating = Math.round((sum / totalRatingsCount) * 100.0) / 100.0;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalRatingsCount() {
        return totalRatingsCount;
    }

    @Override
    public int getDurationSeconds() {
        return modules.stream().mapToInt(Module::getDurationSeconds).sum();
    }

    @Override
    public int getLectureCount() {
        return modules.stream().mapToInt(Module::getLectureCount).sum();
    }
}

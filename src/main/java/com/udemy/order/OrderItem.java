package com.udemy.order;

import com.udemy.model.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderItem {
    private final Course course;
    private final double price;
    private final List<String> appliedAddons;

    public OrderItem(Course course, double price) {
        this(course, price, Collections.emptyList());
    }

    public OrderItem(Course course, double price, List<String> appliedAddons) {
        this.course = Objects.requireNonNull(course, "course must not be null");
        if (price < 0) {
            throw new IllegalArgumentException("OrderItem price cannot be negative: " + price);
        }
        this.price = price;
        this.appliedAddons = new ArrayList<>(appliedAddons != null ? appliedAddons : Collections.emptyList());
    }

    public Course getCourse() {
        return course;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAppliedAddons() {
        return Collections.unmodifiableList(appliedAddons);
    }
}

package com.udemy.addon;

import com.udemy.model.Course;

import java.util.Objects;

public abstract class CourseAddonDecorator implements CourseProduct {
    protected final CourseProduct wrappedProduct;

    protected CourseAddonDecorator(CourseProduct wrappedProduct) {
        this.wrappedProduct = Objects.requireNonNull(wrappedProduct, "wrappedProduct must not be null");
    }

    @Override
    public Course getCourse() {
        return wrappedProduct.getCourse();
    }
}

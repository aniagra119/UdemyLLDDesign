package com.udemy.strategy;

import com.udemy.model.Course;
import com.udemy.model.User;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Course course, User user) {
        if (course == null) return 0.0;
        return course.getBasePrice();
    }
}

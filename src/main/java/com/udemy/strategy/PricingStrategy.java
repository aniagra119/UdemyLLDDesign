package com.udemy.strategy;

import com.udemy.model.Course;
import com.udemy.model.User;

public interface PricingStrategy {
    double calculatePrice(Course course, User user);
}

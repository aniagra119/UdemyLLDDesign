package com.udemy.strategy;

import com.udemy.model.Course;
import com.udemy.model.User;

public class PromotionalTierPricingStrategy implements PricingStrategy {
    private final double discountPercentage;

    public PromotionalTierPricingStrategy(double discountPercentage) {
        if (discountPercentage < 0.0 || discountPercentage > 100.0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100, got: " + discountPercentage);
        }
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public double calculatePrice(Course course, User user) {
        if (course == null) return 0.0;
        double base = course.getBasePrice();
        double discounted = base * (1.0 - (discountPercentage / 100.0));
        return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
    }
}

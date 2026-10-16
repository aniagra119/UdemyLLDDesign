package com.udemy.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MentorshipDoubtSupportAddon extends CourseAddonDecorator {
    private static final double MENTORSHIP_FEE = 1499.0;

    public MentorshipDoubtSupportAddon(CourseProduct wrappedProduct) {
        super(wrappedProduct);
    }

    @Override
    public String getDescription() {
        return wrappedProduct.getDescription() + " + 1-on-1 TA Mentorship";
    }

    @Override
    public double getPrice() {
        return Math.round((wrappedProduct.getPrice() + MENTORSHIP_FEE) * 100.0) / 100.0;
    }

    @Override
    public List<String> getEntitlements() {
        List<String> list = new ArrayList<>(wrappedProduct.getEntitlements());
        list.add("1_ON_1_TA_MENTORSHIP");
        return Collections.unmodifiableList(list);
    }
}

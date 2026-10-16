package com.udemy.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfflineDownloadAddon extends CourseAddonDecorator {
    private static final double OFFLINE_FEE = 299.0;

    public OfflineDownloadAddon(CourseProduct wrappedProduct) {
        super(wrappedProduct);
    }

    @Override
    public String getDescription() {
        return wrappedProduct.getDescription() + " + Offline Mobile Download";
    }

    @Override
    public double getPrice() {
        return Math.round((wrappedProduct.getPrice() + OFFLINE_FEE) * 100.0) / 100.0;
    }

    @Override
    public List<String> getEntitlements() {
        List<String> list = new ArrayList<>(wrappedProduct.getEntitlements());
        list.add("OFFLINE_MOBILE_DOWNLOAD");
        return Collections.unmodifiableList(list);
    }
}

package com.udemy.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerifiedCertificateAddon extends CourseAddonDecorator {
    private static final double CERTIFICATE_FEE = 499.0;

    public VerifiedCertificateAddon(CourseProduct wrappedProduct) {
        super(wrappedProduct);
    }

    @Override
    public String getDescription() {
        return wrappedProduct.getDescription() + " + Verified ID Certificate";
    }

    @Override
    public double getPrice() {
        return Math.round((wrappedProduct.getPrice() + CERTIFICATE_FEE) * 100.0) / 100.0;
    }

    @Override
    public List<String> getEntitlements() {
        List<String> list = new ArrayList<>(wrappedProduct.getEntitlements());
        list.add("VERIFIED_ID_CERTIFICATE");
        return Collections.unmodifiableList(list);
    }
}

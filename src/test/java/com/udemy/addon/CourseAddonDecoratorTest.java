package com.udemy.addon;

import com.udemy.model.Course;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Decorator Pattern: Course Add-ons & Dynamic Entitlements Tests")
class CourseAddonDecoratorTest {

    @Test
    @DisplayName("Decorator: Dynamically adds verified certificate, mentorship, and offline download to course")
    void testDynamicCourseAddons() {
        Course course = new Course("High-Frequency Trading in Java", "Low-latency systems", 5000.0);

        // 1. Base Product
        CourseProduct product = new BaseCourseProduct(course);
        assertEquals(5000.0, product.getPrice());
        assertEquals(List.of("STANDARD_VIDEO_STREAMING"), product.getEntitlements());

        // 2. Wrap with Verified Certificate (+₹499)
        product = new VerifiedCertificateAddon(product);
        assertEquals(5499.0, product.getPrice());
        assertTrue(product.getEntitlements().contains("VERIFIED_ID_CERTIFICATE"));
        assertTrue(product.getDescription().contains("Verified ID Certificate"));

        // 3. Wrap with 1-on-1 Mentorship (+₹1499)
        product = new MentorshipDoubtSupportAddon(product);
        assertEquals(6998.0, product.getPrice()); // 5499 + 1499 = 6998
        assertTrue(product.getEntitlements().contains("1_ON_1_TA_MENTORSHIP"));

        // 4. Wrap with Offline Downloads (+₹299)
        product = new OfflineDownloadAddon(product);
        assertEquals(7297.0, product.getPrice()); // 6998 + 299 = 7297
        assertTrue(product.getEntitlements().contains("OFFLINE_MOBILE_DOWNLOAD"));
        assertEquals(4, product.getEntitlements().size());
    }
}

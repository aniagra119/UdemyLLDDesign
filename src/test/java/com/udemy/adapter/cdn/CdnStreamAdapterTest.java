package com.udemy.adapter.cdn;

import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.Lecture;
import com.udemy.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Adapter Pattern: Multi-Cloud Video Streaming & CDN Tests")
class CdnStreamAdapterTest {

    @Test
    @DisplayName("AWS CloudFront Adapter: Signs URL with Key-Pair-Id and expiry timestamp")
    void testAwsCloudFrontAdapter() {
        CdnStreamAdapter adapter = new AwsCloudFrontAdapter("d987.cloudfront.net", "APK12345678");
        assertEquals("AWS_CLOUDFRONT", adapter.getProviderName());

        String streamUrl = adapter.generateSecureStreamUrl("courses/c1/lec1.mp4", "student_42", 3600);

        assertTrue(streamUrl.startsWith("https://d987.cloudfront.net/courses/c1/lec1.mp4"));
        assertTrue(streamUrl.contains("Key-Pair-Id=APK12345678"));
        assertTrue(streamUrl.contains("Expires="));
        assertTrue(streamUrl.contains("Signature="));
    }

    @Test
    @DisplayName("Cloudflare Stream Adapter: Generates HLS manifest with HMAC token and student identifier")
    void testCloudflareStreamAdapter() {
        CdnStreamAdapter adapter = new CloudflareStreamAdapter("customer-sub", "cf_secret_token_xyz");
        assertEquals("CLOUDFLARE_STREAM", adapter.getProviderName());

        String streamUrl = adapter.generateSecureStreamUrl("media-uid-9999", "student_42", 3600);

        assertTrue(streamUrl.startsWith("https://videodelivery.net/media-uid-9999/manifest/video.m3u8"));
        assertTrue(streamUrl.contains("token="));
        assertTrue(streamUrl.contains("user=student_42"));
    }

    @Test
    @DisplayName("Course Integration: Dynamically switches streaming CDN adapters")
    void testDynamicCdnSwitchingOnCourse() {
        Course course = new Course("Streamed Course", "CDN testing", 999.0);
        Lecture lecture = new Lecture("Video Lecture", 1, new VideoContent("videos/sample.mp4", 600));
        Student student = new Student("stud@test.com", "Test Student");

        // 1. Without adapter: returns raw URL
        assertEquals("videos/sample.mp4", course.generateStreamUrl(lecture, student, 1800));

        // 2. Attach AWS CloudFront adapter
        course.setCdnAdapter(new AwsCloudFrontAdapter("aws.cdn.com", "KEY1"));
        String awsUrl = course.generateStreamUrl(lecture, student, 1800);
        assertTrue(awsUrl.contains("aws.cdn.com"));
        assertTrue(awsUrl.contains("Key-Pair-Id=KEY1"));

        // 3. Hot-swap to Cloudflare Stream adapter
        course.setCdnAdapter(new CloudflareStreamAdapter("cf-sub", "SECRET"));
        String cfUrl = course.generateStreamUrl(lecture, student, 1800);
        assertTrue(cfUrl.contains("videodelivery.net"));
        assertTrue(cfUrl.contains("user=" + student.getId()));
    }
}

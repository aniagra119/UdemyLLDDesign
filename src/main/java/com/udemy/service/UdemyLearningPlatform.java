package com.udemy.service;

import com.udemy.event.CourseEventListener;
import com.udemy.event.CourseEventPublisher;
import com.udemy.exception.UnenrolledStudentException;
import com.udemy.model.Course;
import com.udemy.model.Review;
import com.udemy.model.Student;
import com.udemy.order.Order;
import com.udemy.order.OrderItem;
import com.udemy.order.OrderService;
import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessorFactory;
import com.udemy.progress.Enrollment;
import com.udemy.progress.PlaybackResumeSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class UdemyLearningPlatform {
    private final CourseCatalogService catalogService;
    private final EnrollmentService enrollmentService;
    private final PlaybackService playbackService;
    private final ReviewService reviewService;
    private final CourseEventPublisher eventPublisher;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final OrderService orderService;

    public UdemyLearningPlatform() {
        this.catalogService = new CourseCatalogService();
        this.enrollmentService = new EnrollmentService();
        this.playbackService = new PlaybackService();
        this.reviewService = new ReviewService();
        this.eventPublisher = new CourseEventPublisher();
        this.paymentProcessorFactory = new PaymentProcessorFactory();
        this.orderService = new OrderService(paymentProcessorFactory, enrollmentService);
    }

    public Order purchaseCourses(Student student, List<OrderItem> items, PaymentMethod paymentMethod, Map<String, String> paymentDetails) {
        return orderService.checkout(student, items, paymentMethod, paymentDetails);
    }

    public void registerCourse(Course course) {
        catalogService.registerCourse(course);
    }

    public void publishCourse(String courseId) {
        catalogService.publishCourse(courseId);
    }

    public List<Course> getPublishedCourses() {
        return catalogService.getPublishedCourses();
    }

    public Enrollment enroll(Student student, String courseId) {
        Course course = catalogService.getCourse(courseId);
        return enrollmentService.enroll(student, course);
    }

    public void recordPlaybackHeartbeat(Student student, String courseId, String lectureId, int currentTimestampSeconds) {
        Enrollment enrollment = enrollmentService.getEnrollment(student.getId(), courseId)
                .orElseThrow(() -> new UnenrolledStudentException("Student is not enrolled in course " + courseId));

        boolean wasCompletedBefore = enrollment.isCompleted();
        playbackService.recordHeartbeat(enrollment, lectureId, currentTimestampSeconds);

        if (!wasCompletedBefore && enrollment.isCompleted()) {
            eventPublisher.publishCourseCompleted(enrollment);
        }
    }

    public Optional<PlaybackResumeSnapshot> resumePlayback(Student student, String courseId) {
        Enrollment enrollment = enrollmentService.getEnrollment(student.getId(), courseId)
                .orElseThrow(() -> new UnenrolledStudentException("Student is not enrolled in course " + courseId));

        return playbackService.resumePlayback(enrollment);
    }

    public Review rateCourse(Student student, String courseId, double rating, String comment) {
        Course course = catalogService.getCourse(courseId);
        return reviewService.submitReview(student, course, rating, comment);
    }

    public void registerEventListener(CourseEventListener listener) {
        eventPublisher.subscribe(listener);
    }

    public CourseCatalogService getCatalogService() {
        return catalogService;
    }

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    public PlaybackService getPlaybackService() {
        return playbackService;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public PaymentProcessorFactory getPaymentProcessorFactory() {
        return paymentProcessorFactory;
    }

    public OrderService getOrderService() {
        return orderService;
    }
}

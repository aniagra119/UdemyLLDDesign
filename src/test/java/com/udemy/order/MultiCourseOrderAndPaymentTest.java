package com.udemy.order;

import com.udemy.enums.AuthorshipRole;
import com.udemy.media.VideoContent;
import com.udemy.model.Course;
import com.udemy.model.CourseAuthorship;
import com.udemy.model.Instructor;
import com.udemy.model.Lecture;
import com.udemy.model.Module;
import com.udemy.model.Student;
import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessorFactory;
import com.udemy.payment.PaymentStatus;
import com.udemy.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multi-Course Order Checkout & Payment Gateway SPI Tests")
class MultiCourseOrderAndPaymentTest {

    private OrderService orderService;
    private EnrollmentService enrollmentService;
    private Student student;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService();
        PaymentProcessorFactory paymentFactory = new PaymentProcessorFactory();
        orderService = new OrderService(paymentFactory, enrollmentService);

        student = new Student("buyer@tech.com", "Ananya Verma");
        Instructor instructor = new Instructor("lead@tech.com", "Anirudh Agrawal", "Systems");

        // Course 1
        course1 = new Course("Distributed Caching & Redis", "High-throughput memory grids", 3999.0);
        course1.addAuthorship(new CourseAuthorship(course1.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));
        Module m1 = new Module("M1", 1);
        m1.addLecture(new Lecture("L1", 1, new VideoContent("http://v1.mp4", 600)));
        course1.addModule(m1);
        course1.publish();

        // Course 2
        course2 = new Course("Event-Driven Architecture with Kafka", "Streaming patterns", 4999.0);
        course2.addAuthorship(new CourseAuthorship(course2.getId(), instructor, AuthorshipRole.LEAD_INSTRUCTOR, 100.0));
        Module m2 = new Module("M2", 1);
        m2.addLecture(new Lecture("L2", 1, new VideoContent("http://v2.mp4", 900)));
        course2.addModule(m2);
        course2.publish();
    }

    @Test
    @DisplayName("Requirements: User buys multiple courses in a single checkout via Credit Card")
    void testMultiCourseCheckoutWithCreditCard() {
        List<OrderItem> items = List.of(
                new OrderItem(course1, course1.getBasePrice()),
                new OrderItem(course2, course2.getBasePrice())
        );

        Map<String, String> cardDetails = Map.of("cardNumber", "4111222233334444");
        Order order = orderService.checkout(student, items, PaymentMethod.CREDIT_CARD, cardDetails);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(8998.0, order.getTotalAmount());
        assertEquals(PaymentStatus.SUCCESS, order.getPaymentResult().getStatus());

        // Atomically enrolled in both courses
        assertTrue(student.isEnrolled(course1.getId()), "Student must be enrolled in Course 1");
        assertTrue(student.isEnrolled(course2.getId()), "Student must be enrolled in Course 2");
        assertEquals(2, student.getEnrolledCourseIds().size());
    }

    @Test
    @DisplayName("Payment Strategy SPI: Successfully checkout using UPI VPA")
    void testCheckoutWithUpi() {
        List<OrderItem> items = List.of(new OrderItem(course1, course1.getBasePrice()));
        Map<String, String> upiDetails = Map.of("vpa", "ananya@okaxis");

        Order order = orderService.checkout(student, items, PaymentMethod.UPI, upiDetails);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(order.getPaymentResult().getMessage().contains("NPCI switch"));
        assertTrue(student.isEnrolled(course1.getId()));
    }

    @Test
    @DisplayName("Payment Failure: Invalid credentials mark order FAILED and do not enroll student")
    void testPaymentFailureRejection() {
        List<OrderItem> items = List.of(new OrderItem(course1, course1.getBasePrice()));
        Map<String, String> invalidCard = Map.of("cardNumber", "123"); // Too short

        Order order = orderService.checkout(student, items, PaymentMethod.CREDIT_CARD, invalidCard);

        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertFalse(order.getPaymentResult().isSuccessful());
        assertFalse(student.isEnrolled(course1.getId()), "Student must NOT be enrolled upon failed payment");
    }
}

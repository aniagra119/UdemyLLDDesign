package com.udemy.order;

import com.udemy.model.Student;
import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessor;
import com.udemy.payment.PaymentProcessorFactory;
import com.udemy.payment.PaymentRequest;
import com.udemy.payment.PaymentResult;
import com.udemy.service.EnrollmentService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderService {
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final EnrollmentService enrollmentService;

    public OrderService(PaymentProcessorFactory paymentProcessorFactory, EnrollmentService enrollmentService) {
        this.paymentProcessorFactory = Objects.requireNonNull(paymentProcessorFactory, "paymentProcessorFactory must not be null");
        this.enrollmentService = Objects.requireNonNull(enrollmentService, "enrollmentService must not be null");
    }

    public Order checkout(Student student, List<OrderItem> items, PaymentMethod paymentMethod, Map<String, String> paymentDetails) {
        Objects.requireNonNull(student, "student must not be null");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout with an empty order");
        }

        Order order = new Order(student);
        for (OrderItem item : items) {
            order.addItem(item);
        }

        PaymentProcessor processor = paymentProcessorFactory.getProcessor(paymentMethod);
        PaymentRequest request = new PaymentRequest(order.getId(), student.getId(), order.getTotalAmount(), "INR", paymentMethod, paymentDetails);

        PaymentResult result = processor.process(request);

        if (result.isSuccessful()) {
            order.markPaid(result);
            // Atomically enroll student in each course in the purchased order
            for (OrderItem item : items) {
                enrollmentService.enroll(student, item.getCourse());
            }
        } else {
            order.markFailed(result);
        }

        return order;
    }
}

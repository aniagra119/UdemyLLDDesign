package com.udemy.payment;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class PaymentRequest {
    private final String orderId;
    private final String studentId;
    private final double amount;
    private final String currency;
    private final PaymentMethod paymentMethod;
    private final Map<String, String> paymentDetails;

    public PaymentRequest(String orderId, String studentId, double amount, String currency,
                          PaymentMethod paymentMethod, Map<String, String> paymentDetails) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.studentId = Objects.requireNonNull(studentId, "studentId must not be null");
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative: " + amount);
        }
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod must not be null");
        this.paymentDetails = paymentDetails != null ? paymentDetails : Collections.emptyMap();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Map<String, String> getPaymentDetails() {
        return Collections.unmodifiableMap(paymentDetails);
    }
}

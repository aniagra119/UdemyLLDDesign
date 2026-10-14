package com.udemy.payment;

public interface PaymentProcessor {
    PaymentResult process(PaymentRequest request);
    PaymentMethod getSupportedMethod();
}

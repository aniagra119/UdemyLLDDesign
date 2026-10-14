package com.udemy.payment.processor;

import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessor;
import com.udemy.payment.PaymentRequest;
import com.udemy.payment.PaymentResult;

public class PayPalPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult process(PaymentRequest request) {
        String payerEmail = request.getPaymentDetails().get("payerEmail");
        if (payerEmail == null || !payerEmail.contains("@")) {
            return PaymentResult.failed("Invalid PayPal account email: " + payerEmail);
        }
        return PaymentResult.success("PayPal capture of $" + request.getAmount() + " completed for " + payerEmail);
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.PAYPAL;
    }
}

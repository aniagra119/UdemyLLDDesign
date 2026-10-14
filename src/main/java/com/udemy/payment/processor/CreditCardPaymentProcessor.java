package com.udemy.payment.processor;

import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessor;
import com.udemy.payment.PaymentRequest;
import com.udemy.payment.PaymentResult;

public class CreditCardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult process(PaymentRequest request) {
        String cardNumber = request.getPaymentDetails().get("cardNumber");
        if (cardNumber == null || cardNumber.length() < 12) {
            return PaymentResult.failed("Invalid card credentials for card: " + cardNumber);
        }
        return PaymentResult.success("Card payment of ₹" + request.getAmount() + " processed successfully via Stripe EMV Gateway.");
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.CREDIT_CARD;
    }
}

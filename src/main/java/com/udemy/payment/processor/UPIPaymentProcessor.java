package com.udemy.payment.processor;

import com.udemy.payment.PaymentMethod;
import com.udemy.payment.PaymentProcessor;
import com.udemy.payment.PaymentRequest;
import com.udemy.payment.PaymentResult;

public class UPIPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult process(PaymentRequest request) {
        String vpa = request.getPaymentDetails().get("vpa");
        if (vpa == null || !vpa.contains("@")) {
            return PaymentResult.failed("Invalid UPI VPA identifier: " + vpa);
        }
        return PaymentResult.success("UPI payment of ₹" + request.getAmount() + " approved via NPCI switch (" + vpa + ").");
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.UPI;
    }
}

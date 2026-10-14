package com.udemy.payment;

import com.udemy.payment.processor.CreditCardPaymentProcessor;
import com.udemy.payment.processor.PayPalPaymentProcessor;
import com.udemy.payment.processor.UPIPaymentProcessor;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PaymentProcessorFactory {
    private final Map<PaymentMethod, PaymentProcessor> registry = new EnumMap<>(PaymentMethod.class);

    public PaymentProcessorFactory() {
        register(new CreditCardPaymentProcessor());
        register(new UPIPaymentProcessor());
        register(new PayPalPaymentProcessor());
    }

    public void register(PaymentProcessor processor) {
        Objects.requireNonNull(processor, "processor must not be null");
        registry.put(processor.getSupportedMethod(), processor);
    }

    public PaymentProcessor getProcessor(PaymentMethod method) {
        return Optional.ofNullable(registry.get(method))
                .orElseThrow(() -> new IllegalArgumentException("No payment processor registered for method: " + method));
    }
}

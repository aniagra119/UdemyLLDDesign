package com.udemy.payment;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PaymentResult {
    private final String transactionId;
    private final PaymentStatus status;
    private final String message;
    private final LocalDateTime processedAt;

    public PaymentResult(String transactionId, PaymentStatus status, String message) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.message = message != null ? message : "";
        this.processedAt = LocalDateTime.now();
    }

    public static PaymentResult success(String message) {
        return new PaymentResult("TXN-" + UUID.randomUUID(), PaymentStatus.SUCCESS, message);
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult("FAIL-" + UUID.randomUUID(), PaymentStatus.FAILED, message);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}

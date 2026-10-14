package com.udemy.order;

import com.udemy.model.BaseEntity;
import com.udemy.model.Student;
import com.udemy.payment.PaymentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order extends BaseEntity {
    private final Student student;
    private final List<OrderItem> items;
    private double totalAmount;
    private OrderStatus status;
    private PaymentResult paymentResult;

    public Order(Student student) {
        super();
        this.student = Objects.requireNonNull(student, "student must not be null");
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
        this.status = OrderStatus.CREATED;
    }

    public Student getStudent() {
        return student;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to order in status: " + status);
        }
        if (item != null) {
            this.items.add(item);
            this.totalAmount += item.getPrice();
            markUpdated();
        }
    }

    public double getTotalAmount() {
        return Math.round(totalAmount * 100.0) / 100.0;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void markPaid(PaymentResult result) {
        this.paymentResult = Objects.requireNonNull(result, "paymentResult must not be null");
        this.status = OrderStatus.PAID;
        markUpdated();
    }

    public void markFailed(PaymentResult result) {
        this.paymentResult = result;
        this.status = OrderStatus.FAILED;
        markUpdated();
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }
}

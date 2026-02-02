package com.training.common.enums;

public enum PaymentStatus implements EventType {
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,
    REFUND_REQUESTED,
    REFUND_COMPLETED
}

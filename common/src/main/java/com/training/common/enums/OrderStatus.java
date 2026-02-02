package com.training.common.enums;

public enum OrderStatus implements EventType {
    ORDER_CREATED,
    ORDER_CANCELED,
    ORDER_ACCEPTED,
    ORDER_REJECTED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    REFUND_REQUESTED,
    REFUNDED,
    REFUND_FAILED
}

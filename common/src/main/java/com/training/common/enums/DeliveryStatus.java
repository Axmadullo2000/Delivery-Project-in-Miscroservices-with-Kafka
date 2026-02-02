package com.training.common.enums;

public enum DeliveryStatus implements EventType {
    DELIVERY_CREATED,
    DELIVERY_ASSIGNED,
    DELIVERY_IN_TRANSIT,
    DELIVERY_COMPLETED,
    DELIVERY_CANCELLED,
    DELIVERY_UNAVAILABLE,
    RESTAURANT_ORDER_CANCELLED
}

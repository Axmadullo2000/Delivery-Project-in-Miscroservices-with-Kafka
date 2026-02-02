package com.training.deliveryservice.entity;


public enum DeliveryStatus {
    PENDING,        // Waiting for courier assignment
    ASSIGNED,       // Courier has been assigned
    IN_TRANSIT,     // Courier is on the way
    DELIVERED,      // Successfully delivered
    CANCELLED       // Delivery was cancelled
}

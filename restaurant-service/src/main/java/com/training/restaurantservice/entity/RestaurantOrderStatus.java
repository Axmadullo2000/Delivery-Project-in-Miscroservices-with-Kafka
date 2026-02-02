package com.training.restaurantservice.entity;


public enum RestaurantOrderStatus {
    RECEIVED,       // Order received from customer
    PREPARING,      // Kitchen is preparing the order
    READY,          // Order is ready for pickup/delivery
    COMPLETED,      // Order has been picked up
    CANCELLED       // Order was cancelled
}

package com.training.orderservice.entity;


public enum OrderStatus {
    CREATED,              // Заказ создан
    ACCEPTED,             // Ресторан принял
    REJECTED,             // Ресторан отклонил
    PAYMENT_COPLETED,    // Оплата прошла
    PAYMENT_FAILED,       // Оплата не прошла
    DELIVERY_ASSIGNED,    // Курьер назначен
    DELIVERY_UNAVAILABLE, // Курьер не найден
    DELIVERED,            // Доставлено
    CANCELLED             // Отменён
}

package com.training.orderservice.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {
    List<Orders> findByCustomerId(String customerId);

    List<Orders> findByRestaurantId(String restaurantId);
}

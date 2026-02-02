package com.training.restaurantservice.repo;

import com.training.restaurantservice.entity.RestaurantOrder;
import com.training.restaurantservice.entity.RestaurantOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, String> {

    Optional<RestaurantOrder> findByOrderId(String orderId);

    List<RestaurantOrder> findByRestaurantId(String restaurantId);

    List<RestaurantOrder> findByRestaurantIdAndStatus(String restaurantId, RestaurantOrderStatus status);

    List<RestaurantOrder> findByStatus(RestaurantOrderStatus status);

    boolean existsByOrderId(String orderId);
}

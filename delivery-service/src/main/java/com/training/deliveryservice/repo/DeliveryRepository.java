package com.training.deliveryservice.repo;

import com.training.deliveryservice.entity.Delivery;
import com.training.deliveryservice.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {

    Optional<Delivery> findByOrderId(String orderId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByCourierId(String courierId);

    List<Delivery> findByCustomerId(String customerId);

    boolean existsByOrderId(String orderId);
}

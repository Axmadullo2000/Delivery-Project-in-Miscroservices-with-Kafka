package com.training.orderservice.service;

import com.training.common.enums.OrderStatus;
import com.training.common.event.order.OrderCancelledEvent;
import com.training.orderservice.entity.Orders;
import com.training.orderservice.entity.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final OrdersRepository ordersRepository;

    private static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";

    /**
     * Listens for OrderCancelledEvent from Restaurant Service
     */
    @KafkaListener(
            topics = RESTAURANT_EVENTS_TOPIC,
            groupId = "order-service-restaurant-group",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event) {
        String orderId = event.getOrderId();
        log.info("Received OrderCancelledEvent: orderId={}, reason={}, cancelledBy={}",
                orderId, event.getReason(), event.getCancelledBy());

        ordersRepository.findById(orderId).ifPresentOrElse(
                order -> {
                    // Only update if not already cancelled or refunded
                    if (order.getStatus() != OrderStatus.ORDER_CANCELED &&
                        order.getStatus() != OrderStatus.REFUNDED) {
                        order.setStatus(OrderStatus.ORDER_CANCELED);
                        order.setUpdatedAt(LocalDateTime.now());
                        ordersRepository.save(order);
                        log.info("Order marked as cancelled: orderId={}, reason={}", orderId, event.getReason());
                    } else {
                        log.info("Order already in terminal state: orderId={}, status={}",
                                orderId, order.getStatus());
                    }
                },
                () -> log.error("Order not found for cancellation: orderId={}", orderId)
        );
    }

}

package com.training.restaurantservice.service;

import com.training.common.dto.OrderDTO;
import com.training.common.event.order.OrderCreatedEvent;
import com.training.common.event.payment.RefundCompletedEvent;
import com.training.common.event.restaurant.OrderAcceptedEvent;
import com.training.common.event.restaurant.OrderRejectedEvent;
import com.training.restaurantservice.entity.Restaurant;
import com.training.restaurantservice.entity.RestaurantOrder;
import com.training.restaurantservice.entity.RestaurantOrderStatus;
import com.training.restaurantservice.repo.RestaurantOrderRepository;
import com.training.restaurantservice.repo.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.training.restaurantservice.config.KafkaTopicConfig.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantEventProducer eventProducer;

    @KafkaListener(topics = ORDER_EVENTS_TOPIC, groupId = "restaurant-service-group")
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        String orderId = null;
        String restaurantId = null;

        try {
            log.info("Received OrderCreatedEvent: orderId={}, eventType={}",
                    event.getOrderId(), event.getEventType());

            OrderDTO order = event.getOrder();
            orderId = event.getOrderId();
            restaurantId = order.getRestaurantId();

            // Business logic: Check if restaurant exists and is open
            Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);

            if (restaurantOpt.isEmpty()) {
                // Restaurant not found - reject order
                log.warn("Restaurant not found: {}, rejecting order: {}", restaurantId, orderId);
                OrderRejectedEvent rejectedEvent = new OrderRejectedEvent(
                        orderId,
                        restaurantId,
                        "Restaurant not found: " + restaurantId
                );
                eventProducer.sendRestaurantEvent(RESTAURANT_EVENTS_TOPIC, orderId, rejectedEvent);
                return;
            }

            Restaurant restaurant = restaurantOpt.get();

            if (!restaurant.getIsOpen()) {
                // Restaurant is closed - reject order
                log.warn("Restaurant is closed: {}, rejecting order: {}", restaurantId, orderId);
                OrderRejectedEvent rejectedEvent = new OrderRejectedEvent(
                        orderId,
                        restaurantId,
                        "Restaurant is currently closed"
                );
                eventProducer.sendRestaurantEvent(RESTAURANT_EVENTS_TOPIC, orderId, rejectedEvent);
                return;
            }

            // Restaurant exists and is open - accept order
            log.info("Accepting order: {} for restaurant: {}", orderId, restaurantId);
            LocalDateTime estimatedTime = LocalDateTime.now()
                    .plusMinutes(restaurant.getAverageCookingTimeMinutes());

            // Save restaurant order to database
            RestaurantOrder restaurantOrder = RestaurantOrder.builder()
                    .orderId(orderId)
                    .restaurantId(restaurantId)
                    .customerId(order.getCustomerId())
                    .status(RestaurantOrderStatus.PREPARING)
                    .estimatedCompletionTime(estimatedTime)
                    .build();
            restaurantOrderRepository.save(restaurantOrder);
            log.info("Restaurant order saved: orderId={}, restaurantOrderId={}",
                    orderId, restaurantOrder.getId());

            // Publish acceptance event
            OrderAcceptedEvent acceptedEvent = new OrderAcceptedEvent(
                    orderId,
                    restaurantId,
                    estimatedTime
            );
            eventProducer.sendRestaurantEvent(RESTAURANT_EVENTS_TOPIC, orderId, acceptedEvent);

        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for orderId={}: {}", orderId, e.getMessage(), e);
            // Send rejection event on error
            if (orderId != null) {
                OrderRejectedEvent rejectedEvent = new OrderRejectedEvent(
                        orderId,
                        restaurantId != null ? restaurantId : "unknown",
                        "Processing error: " + e.getMessage()
                );
                eventProducer.sendRestaurantEvent(RESTAURANT_EVENTS_TOPIC, orderId, rejectedEvent);
            }
        }
    }

    /**
     * Listens for RefundCompletedEvent to cancel restaurant order when payment is refunded
     */
    @KafkaListener(
            topics = PAYMENT_EVENTS_TOPIC,
            groupId = "restaurant-service-refund-group",
            containerFactory = "refundKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleRefundCompleted(RefundCompletedEvent event) {
        String orderId = event.getOrderId();
        log.info("Received RefundCompletedEvent: orderId={}, status={}",
                orderId, event.getStatus());

        if ("SUCCESS".equals(event.getStatus())) {
            // Find restaurant order by orderId and cancel it
            restaurantOrderRepository.findByOrderId(orderId)
                    .ifPresentOrElse(
                            restaurantOrder -> {
                                // Cancel the order in restaurant
                                restaurantOrder.setStatus(RestaurantOrderStatus.CANCELLED);
                                restaurantOrder.setCancellationReason("Payment refunded");
                                restaurantOrder.setCancelledBy("SYSTEM");
                                restaurantOrder.setUpdatedAt(LocalDateTime.now());
                                restaurantOrderRepository.save(restaurantOrder);

                                log.info("Restaurant order cancelled due to refund: orderId={}, restaurantOrderId={}",
                                        orderId, restaurantOrder.getId());

                                // Publish order cancelled event
                                eventProducer.publishOrderCancelled(
                                        orderId,
                                        restaurantOrder.getRestaurantId(),
                                        "Payment refunded"
                                );
                            },
                            () -> log.warn("Restaurant order not found for cancellation: orderId={}", orderId)
                    );
        } else {
            log.info("Refund failed, no action needed in restaurant: orderId={}, reason={}",
                    orderId, event.getFailureReason());
        }
    }

}

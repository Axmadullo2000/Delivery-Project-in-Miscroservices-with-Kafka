package com.training.deliveryservice.service;

import com.training.common.event.order.OrderCancelledEvent;
import com.training.deliveryservice.entity.Delivery;
import com.training.deliveryservice.entity.DeliveryStatus;
import com.training.deliveryservice.repo.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.training.deliveryservice.config.KafkaTopicConfig.RESTAURANT_EVENTS_TOPIC;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer eventProducer;

    /**
     * Listens for OrderCancelledEvent from Restaurant Service.
     * Cancels the delivery when the order is cancelled.
     */
    @KafkaListener(
            topics = RESTAURANT_EVENTS_TOPIC,
            groupId = "delivery-service-restaurant-group",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event) {
        String orderId = event.getOrderId();
        log.info("Received OrderCancelledEvent: orderId={}, reason={}, cancelledBy={}",
                orderId, event.getReason(), event.getCancelledBy());

        deliveryRepository.findByOrderId(orderId).ifPresentOrElse(
                delivery -> {
                    // Only cancel if not already delivered or cancelled
                    if (delivery.getStatus() != DeliveryStatus.DELIVERED &&
                        delivery.getStatus() != DeliveryStatus.CANCELLED) {

                        delivery.setStatus(DeliveryStatus.CANCELLED);
                        delivery.setCancellationReason("Order cancelled: " + event.getReason());
                        delivery.setUpdatedAt(LocalDateTime.now());
                        deliveryRepository.save(delivery);

                        log.info("Delivery cancelled due to order cancellation: deliveryId={}, orderId={}",
                                delivery.getId(), orderId);

                        // Publish DeliveryCancelledEvent
                        eventProducer.publishDeliveryCancelled(
                                orderId,
                                delivery.getId(),
                                "Order cancelled: " + event.getReason()
                        );
                    } else {
                        log.info("Delivery already in terminal state: deliveryId={}, status={}",
                                delivery.getId(), delivery.getStatus());
                    }
                },
                () -> log.info("No delivery found for cancelled order: orderId={}", orderId)
        );
    }

}

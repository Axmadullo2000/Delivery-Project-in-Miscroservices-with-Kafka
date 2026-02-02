package com.training.restaurantservice.service;

import com.training.common.event.BaseEvent;
import com.training.common.event.order.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.training.restaurantservice.config.KafkaTopicConfig.RESTAURANT_EVENTS_TOPIC;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantEventProducer {
    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;

    public void sendRestaurantEvent(String topic, String orderId, BaseEvent<?> event) {
        log.info("Sending event to topic: {}, orderId: {}, eventType: {}", topic, orderId, event.getEventType());

        kafkaTemplate.send(topic, orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully: topic={}, orderId={}, eventType={}, partition={}, offset={}",
                                topic,
                                orderId,
                                event.getEventType(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event to topic: {}, orderId: {}, error: {}",
                                topic, orderId, ex.getMessage(), ex);
                    }
                });
    }

    /**
     * Publishes OrderCancelledEvent when restaurant cancels an order (e.g., due to refund)
     */
    public void publishOrderCancelled(String orderId, String restaurantId, String reason) {
        publishOrderCancelled(orderId, restaurantId, reason, "SYSTEM");
    }

    /**
     * Publishes OrderCancelledEvent when an order is cancelled
     * @param orderId - the order ID
     * @param restaurantId - the restaurant ID
     * @param reason - cancellation reason
     * @param cancelledBy - who cancelled: CUSTOMER, RESTAURANT, SYSTEM
     */
    public void publishOrderCancelled(String orderId, String restaurantId, String reason, String cancelledBy) {
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, restaurantId, reason, cancelledBy);

        log.info("Publishing OrderCancelledEvent: orderId={}, restaurantId={}, reason={}, cancelledBy={}",
                orderId, restaurantId, reason, cancelledBy);

        kafkaTemplate.send(RESTAURANT_EVENTS_TOPIC, orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("OrderCancelledEvent published: orderId={}, topic={}, partition={}, offset={}",
                                orderId,
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish OrderCancelledEvent: orderId={}, error={}",
                                orderId, ex.getMessage(), ex);
                    }
                });
    }

}

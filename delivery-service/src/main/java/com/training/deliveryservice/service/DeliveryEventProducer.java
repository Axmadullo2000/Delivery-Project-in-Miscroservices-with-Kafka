package com.training.deliveryservice.service;

import com.training.common.event.BaseEvent;
import com.training.common.event.delivery.DeliveryAssignedEvent;
import com.training.common.event.delivery.DeliveryCancelledEvent;
import com.training.common.event.delivery.DeliveryCompletedEvent;
import com.training.common.event.delivery.DeliveryCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.training.deliveryservice.config.KafkaTopicConfig.DELIVERY_EVENTS_TOPIC;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryEventProducer {

    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;

    public void sendDeliveryEvent(String orderId, BaseEvent<?> event) {
        log.info("Sending event to topic: {}, orderId: {}, eventType: {}",
                DELIVERY_EVENTS_TOPIC, orderId, event.getEventType());

        kafkaTemplate.send(DELIVERY_EVENTS_TOPIC, orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully: topic={}, orderId={}, eventType={}, partition={}, offset={}",
                                DELIVERY_EVENTS_TOPIC,
                                orderId,
                                event.getEventType(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event to topic: {}, orderId: {}, error: {}",
                                DELIVERY_EVENTS_TOPIC, orderId, ex.getMessage(), ex);
                    }
                });
    }

    public void publishDeliveryCreated(String orderId, String deliveryId, String customerId, String deliveryAddress) {
        DeliveryCreatedEvent event = new DeliveryCreatedEvent(orderId, deliveryId, customerId, deliveryAddress);
        sendDeliveryEvent(orderId, event);
        log.info("DeliveryCreatedEvent published: orderId={}, deliveryId={}", orderId, deliveryId);
    }

    public void publishDeliveryAssigned(String orderId, String courierId, String courierName, Integer estimatedMinutes) {
        DeliveryAssignedEvent event = new DeliveryAssignedEvent(orderId, courierId, courierName, estimatedMinutes);
        sendDeliveryEvent(orderId, event);
        log.info("DeliveryAssignedEvent published: orderId={}, courierName={}", orderId, courierName);
    }

    public void publishDeliveryCompleted(String orderId, String courierId) {
        DeliveryCompletedEvent event = new DeliveryCompletedEvent(orderId, courierId);
        sendDeliveryEvent(orderId, event);
        log.info("DeliveryCompletedEvent published: orderId={}", orderId);
    }

    public void publishDeliveryCancelled(String orderId, String deliveryId, String reason) {
        DeliveryCancelledEvent event = new DeliveryCancelledEvent(orderId, deliveryId, reason);
        sendDeliveryEvent(orderId, event);
        log.info("DeliveryCancelledEvent published: orderId={}, reason={}", orderId, reason);
    }

}

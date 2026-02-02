package com.training.orderservice.service;


import com.training.common.event.BaseEvent;
import com.training.common.event.payment.RefundRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;

    private static final String REFUND_EVENTS_TOPIC = "refund-events";

    public void sendOrderEvent(String topic, String orderId, BaseEvent<?> event) {
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
     * Publishes RefundRequestedEvent to Kafka for Payment Service to process
     */
    public void publishRefundRequested(String orderId, String reason) {
        publishRefundRequested(orderId, reason, null);
    }

    /**
     * Publishes RefundRequestedEvent to Kafka for Payment Service to process
     * @param orderId - the order ID
     * @param reason - refund reason
     * @param amount - optional refund amount (null means full refund)
     */
    public void publishRefundRequested(String orderId, String reason, BigDecimal amount) {
        RefundRequestedEvent event = new RefundRequestedEvent(orderId, reason, amount);

        log.info("Publishing RefundRequestedEvent: orderId={}, reason={}, amount={}",
                orderId, reason, amount != null ? amount : "full refund");

        kafkaTemplate.send(REFUND_EVENTS_TOPIC, orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("RefundRequestedEvent published successfully: orderId={}, topic={}, partition={}, offset={}",
                                orderId,
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish RefundRequestedEvent: orderId={}, error={}",
                                orderId, ex.getMessage(), ex);
                    }
                });
    }

}

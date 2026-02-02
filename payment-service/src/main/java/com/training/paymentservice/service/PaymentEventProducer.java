package com.training.paymentservice.service;

import com.training.common.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;

    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    public static final String REFUND_EVENTS_TOPIC = "refund-events";

    public void sendPaymentEvent(String orderId, BaseEvent<?> event) {
        sendEvent(PAYMENT_EVENTS_TOPIC, orderId, event);
    }

    public void sendRefundEvent(String orderId, BaseEvent<?> event) {
        sendEvent(REFUND_EVENTS_TOPIC, orderId, event);
    }

    private void sendEvent(String topic, String orderId, BaseEvent<?> event) {
        log.info("Sending event to topic: {}, orderId: {}, eventType: {}",
                topic, orderId, event.getEventType());

        kafkaTemplate.send(topic, orderId, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully: topic={}, orderId={}, eventType={}",
                                topic,
                                orderId,
                                event.getEventType());
                    } else {
                        log.error("Failed to send event to topic: {}", topic, ex);
                    }
                });
    }

}

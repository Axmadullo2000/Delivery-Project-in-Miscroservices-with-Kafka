package com.training.paymentservice.service;

import com.training.common.event.restaurant.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final PaymentService paymentService;

    private static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";

    @KafkaListener(topics = RESTAURANT_EVENTS_TOPIC, groupId = "payment-group")
    public void handleRestaurantEvent(OrderAcceptedEvent event) {
        log.info("Received OrderAcceptedEvent: orderId={}, eventType={}",
                event.getOrderId(), event.getEventType());

        // Only process OrderAcceptedEvent (OrderRejectedEvent is ignored by type filtering)
        paymentService.processPayment(event);
    }

}

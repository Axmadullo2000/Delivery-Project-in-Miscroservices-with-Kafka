package com.training.paymentservice.service;

import com.training.common.event.payment.RefundRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.training.paymentservice.service.PaymentEventProducer.REFUND_EVENTS_TOPIC;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefundEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = REFUND_EVENTS_TOPIC,
            groupId = "payment-service-refund-group",
            containerFactory = "refundKafkaListenerContainerFactory"
    )
    public void handleRefundRequestedEvent(RefundRequestedEvent event) {
        log.info("Received RefundRequestedEvent: orderId={}, reason={}",
                event.getOrderId(), event.getReason());

        try {
            paymentService.processRefundAsync(event);
        } catch (Exception e) {
            log.error("Error processing RefundRequestedEvent for orderId={}: {}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }

}

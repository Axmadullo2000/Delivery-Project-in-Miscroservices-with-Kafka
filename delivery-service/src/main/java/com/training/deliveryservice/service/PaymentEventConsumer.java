package com.training.deliveryservice.service;

import com.training.common.event.payment.PaymentCompletedEvent;
import com.training.deliveryservice.entity.Delivery;
import com.training.deliveryservice.entity.DeliveryStatus;
import com.training.deliveryservice.repo.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.training.deliveryservice.config.KafkaTopicConfig.PAYMENT_EVENTS_TOPIC;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer eventProducer;

    /**
     * Listens for PaymentCompletedEvent from Payment Service.
     * Creates a new Delivery record when payment is successful.
     */
    @KafkaListener(
            topics = PAYMENT_EVENTS_TOPIC,
            groupId = "delivery-service-group",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        String orderId = event.getOrderId();
        log.info("Received PaymentCompletedEvent: orderId={}, amount={}, transactionId={}",
                orderId, event.getAmount(), event.getTransactionId());

        try {
            // Check if delivery already exists for this order
            if (deliveryRepository.existsByOrderId(orderId)) {
                log.warn("Delivery already exists for order: {}, skipping", orderId);
                return;
            }

            // Create new delivery record
            // Note: In a real system, you would get customer details and address from the order
            Delivery delivery = Delivery.builder()
                    .orderId(orderId)
                    .customerId("customer-" + orderId) // placeholder - should come from order
                    .deliveryAddress("Address for order " + orderId) // placeholder
                    .status(DeliveryStatus.PENDING)
                    .build();

            Delivery savedDelivery = deliveryRepository.save(delivery);
            log.info("Delivery created: deliveryId={}, orderId={}, status={}",
                    savedDelivery.getId(), orderId, savedDelivery.getStatus());

            // Publish DeliveryCreatedEvent
            eventProducer.publishDeliveryCreated(
                    orderId,
                    savedDelivery.getId(),
                    savedDelivery.getCustomerId(),
                    savedDelivery.getDeliveryAddress()
            );

        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent for orderId={}: {}",
                    orderId, e.getMessage(), e);
            throw e; // Let error handler deal with it
        }
    }

}

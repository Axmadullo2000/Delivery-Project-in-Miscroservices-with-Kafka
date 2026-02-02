package com.training.orderservice.service;

import com.training.common.enums.OrderStatus;
import com.training.common.event.payment.RefundCompletedEvent;
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
public class PaymentEventConsumer {

    private final OrdersRepository ordersRepository;

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    /**
     * Listens for RefundCompletedEvent from Payment Service and updates order status accordingly.
     */
    @KafkaListener(
            topics = PAYMENT_EVENTS_TOPIC,
            groupId = "order-service-payment-group",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleRefundCompleted(RefundCompletedEvent event) {
        String orderId = event.getOrderId();
        log.info("Received RefundCompletedEvent: orderId={}, status={}",
                orderId, event.getStatus());

        ordersRepository.findById(orderId).ifPresentOrElse(
                order -> {
                    if ("SUCCESS".equals(event.getStatus())) {
                        order.setStatus(OrderStatus.REFUNDED);
                        order.setUpdatedAt(LocalDateTime.now());
                        ordersRepository.save(order);
                        log.info("Order refunded successfully: orderId={}, refundTxId={}, amount={}",
                                orderId, event.getRefundTransactionId(), event.getAmount());
                    } else {
                        order.setStatus(OrderStatus.REFUND_FAILED);
                        order.setUpdatedAt(LocalDateTime.now());
                        ordersRepository.save(order);
                        log.error("Order refund failed: orderId={}, errorMessage={}",
                                orderId, event.getFailureReason());
                    }
                },
                () -> log.error("Order not found for refund completion: orderId={}", orderId)
        );
    }

}

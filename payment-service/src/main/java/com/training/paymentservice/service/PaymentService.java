package com.training.paymentservice.service;

import com.training.common.event.payment.PaymentCompletedEvent;
import com.training.common.event.payment.PaymentFailedEvent;
import com.training.common.event.payment.PaymentRefundedEvent;
import com.training.common.event.payment.RefundCompletedEvent;
import com.training.common.event.payment.RefundRequestedEvent;
import com.training.common.event.restaurant.OrderAcceptedEvent;
import com.training.paymentservice.dto.PaymentDTO;
import com.training.paymentservice.dto.RefundRequestDTO;
import com.training.paymentservice.entity.Payment;
import com.training.paymentservice.entity.PaymentStatus;
import com.training.paymentservice.mapper.PaymentMapper;
import com.training.paymentservice.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventProducer eventProducer;

    private final Random random = new Random();

    private static final double SUCCESS_RATE = 0.8; // 80% success rate

    @Transactional
    public void processPayment(OrderAcceptedEvent event) {
        String orderId = event.getOrderId();
        log.info("Processing payment for order: {}", orderId);

        // Simulate payment amount (in real scenario, this would come from order details)
        BigDecimal amount = BigDecimal.valueOf(100.00 + random.nextDouble() * 400.00)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        // Simulate payment processing with 80% success rate
        boolean paymentSuccessful = random.nextDouble() < SUCCESS_RATE;

        if (paymentSuccessful) {
            // Payment successful
            String transactionId = UUID.randomUUID().toString();

            Payment payment = Payment.builder()
                    .orderId(orderId)
                    .customerId("customer-" + orderId) // In real scenario, this would come from event
                    .amount(amount)
                    .status(PaymentStatus.COMPLETED)
                    .transactionId(transactionId)
                    .build();

            paymentRepository.save(payment);
            log.info("Payment completed for order: {}, transactionId: {}, amount: {}",
                    orderId, transactionId, amount);

            // Publish PaymentCompletedEvent
            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                    orderId,
                    amount,
                    transactionId
            );
            eventProducer.sendPaymentEvent(orderId, completedEvent);

        } else {
            // Payment failed
            String failureReason = random.nextBoolean()
                    ? "Insufficient funds"
                    : "Payment gateway error";

            Payment payment = Payment.builder()
                    .orderId(orderId)
                    .customerId("customer-" + orderId)
                    .amount(amount)
                    .status(PaymentStatus.FAILED)
                    .build();

            paymentRepository.save(payment);
            log.warn("Payment failed for order: {}, reason: {}", orderId, failureReason);

            // Publish PaymentFailedEvent
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    orderId,
                    failureReason
            );
            eventProducer.sendPaymentEvent(orderId, failedEvent);
        }
    }

    /**
     * Initiates async refund by publishing RefundRequestedEvent to Kafka.
     * The actual processing happens in processRefundAsync().
     */
    public void initiateRefund(RefundRequestDTO request) {
        log.info("Initiating async refund for order: {}, reason: {}", request.getOrderId(), request.getReason());

        RefundRequestedEvent event = new RefundRequestedEvent(
                request.getOrderId(),
                request.getReason(),
                request.getAmount()
        );

        eventProducer.sendRefundEvent(request.getOrderId(), event);
        log.info("RefundRequestedEvent published for order: {}", request.getOrderId());
    }

    /**
     * Processes refund asynchronously (called by RefundEventConsumer).
     * Publishes RefundCompletedEvent with SUCCESS or FAILED status.
     */
    @Transactional
    public void processRefundAsync(RefundRequestedEvent event) {
        String orderId = event.getOrderId();
        log.info("Processing async refund for order: {}, reason: {}", orderId, event.getReason());

        try {
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);

            if (paymentOpt.isEmpty()) {
                log.error("Refund processing failed: Payment not found for order: {}", orderId);
                publishRefundFailed(orderId, "Payment not found for order: " + orderId);
                return;
            }

            Payment payment = paymentOpt.get();
            log.info("Payment found: id={}, status={}, amount={}",
                    payment.getId(), payment.getStatus(), payment.getAmount());

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                String errorMsg = "Cannot refund payment with status: " + payment.getStatus();
                log.error("Refund processing failed: {}", errorMsg);
                publishRefundFailed(orderId, errorMsg);
                return;
            }

            // Process refund
            BigDecimal refundAmount = event.getAmount() != null
                    ? event.getAmount()
                    : payment.getAmount();

            String refundTransactionId = "REFUND-" + UUID.randomUUID().toString();

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            log.info("Payment refunded successfully: orderId={}, amount={}, refundTxId={}",
                    orderId, refundAmount, refundTransactionId);

            // Publish success event
            RefundCompletedEvent completedEvent = new RefundCompletedEvent(
                    orderId,
                    refundAmount,
                    payment.getTransactionId(),
                    refundTransactionId
            );
            eventProducer.sendPaymentEvent(orderId, completedEvent);

        } catch (Exception e) {
            log.error("Refund processing error for order {}: {}", orderId, e.getMessage(), e);
            publishRefundFailed(orderId, "Processing error: " + e.getMessage());
        }
    }

    private void publishRefundFailed(String orderId, String reason) {
        RefundCompletedEvent failedEvent = new RefundCompletedEvent(orderId, reason);
        eventProducer.sendPaymentEvent(orderId, failedEvent);
    }

    /**
     * @deprecated Use initiateRefund() for async processing instead.
     */
    @Deprecated
    @Transactional
    public PaymentDTO refundPaymentSync(String orderId, String reason) {
        log.info("=== SYNC REFUND START === order: {}, reason: {}", orderId, reason);

        try {
            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

            log.info("Payment found: id={}, status={}, amount={}",
                    payment.getId(), payment.getStatus(), payment.getAmount());

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                throw new RuntimeException("Cannot refund payment with status: " + payment.getStatus());
            }

            payment.setStatus(PaymentStatus.REFUNDED);
            log.info("Status changed to REFUNDED");

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved to DB");

            // Publish event
            PaymentRefundedEvent refundedEvent = new PaymentRefundedEvent(
                    orderId,
                    payment.getAmount(),
                    payment.getTransactionId(),
                    reason
            );
            log.info("Event created, sending to Kafka...");

            eventProducer.sendPaymentEvent(orderId, refundedEvent);
            log.info("Event sent to Kafka");

            log.info("Mapping to DTO...");
            PaymentDTO dto = paymentMapper.toDto(savedPayment);
            log.info("DTO mapped successfully: {}", dto);

            return dto;

        } catch (Exception e) {
            log.error("!!! REFUND ERROR !!!", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return paymentMapper.toDto(payment);
    }

}

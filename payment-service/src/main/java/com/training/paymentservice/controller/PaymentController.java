package com.training.paymentservice.controller;

import com.training.paymentservice.dto.PaymentDTO;
import com.training.paymentservice.dto.RefundRequestDTO;
import com.training.paymentservice.dto.RefundResponseDTO;
import com.training.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.training.common.enums.RefundStatus.PROCESSING;


@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.info("Received get all payments request");
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable String orderId) {
        log.info("Received get payment request for order: {}", orderId);
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Initiates async refund process via Kafka.
     * Returns 202 Accepted immediately, actual refund happens asynchronously.
     */
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<RefundResponseDTO> refundPayment(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "Customer requested refund") String reason,
            @RequestParam BigDecimal refundAmount
            ) {
        log.info("Received async refund request for order: {}, reason: {}", orderId, reason);

        RefundRequestDTO request = new RefundRequestDTO(orderId, reason, refundAmount);
        paymentService.initiateRefund(request);

        RefundResponseDTO response = new RefundResponseDTO(
                orderId,
                PROCESSING,
                "Refund request accepted and is being processed"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}

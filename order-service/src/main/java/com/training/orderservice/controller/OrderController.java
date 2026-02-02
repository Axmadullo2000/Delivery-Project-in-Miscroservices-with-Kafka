package com.training.orderservice.controller;


import com.training.common.dto.OrderDTO;
import com.training.orderservice.dto.RefundRequest;
import com.training.orderservice.dto.RefundRequestDTO;
import com.training.orderservice.dto.RefundResponseDTO;
import com.training.orderservice.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        log.info("Received create order request: {}", orderDTO);
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/api/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String orderId) {
        log.info("Received get order request: {}", orderId);
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/api/orders/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(@PathVariable String customerId) {
        log.info("Received get orders by customer id request: {}", customerId);
        List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        log.info("Received get all orders");
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Initiates async refund for an order (alternative endpoint).
     * Returns 202 Accepted immediately, refund processing happens asynchronously via Kafka.
     */
    @PostMapping("/api/orders/{orderId}/refund")
    public ResponseEntity<RefundResponseDTO> requestRefundViaOrders(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "Customer requested refund") String reason) {
        log.info("Received refund request for order: {}, reason: {}", orderId, reason);

        RefundRequestDTO request = new RefundRequestDTO(orderId, reason, null);
        orderService.initiateRefund(request);

        RefundResponseDTO response = new RefundResponseDTO(
                orderId,
                "PROCESSING",
                "Refund request accepted and is being processed asynchronously"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Request refund for an order.
     * This endpoint accepts refund request from client and delegates processing to Payment Service via Kafka.
     * Returns 202 Accepted immediately, actual refund happens asynchronously.
     */
    @PostMapping("/api/payments/refund/{orderId}")
    public ResponseEntity<Map<String, String>> requestRefund(
            @PathVariable String orderId,
            @Valid @RequestBody RefundRequest request) {

        log.info("Refund requested for order: {}, reason: {}", orderId, request.getReason());

        // Validate order and initiate refund (throws exception if order not found or invalid status)
        orderService.validateAndInitiateRefund(orderId, request.getReason());

        // Return immediate response to client
        return ResponseEntity.accepted().body(Map.of(
                "orderId", orderId,
                "status", "REFUND_REQUESTED",
                "message", "Refund request is being processed asynchronously. You will be notified once completed."
        ));
    }

}

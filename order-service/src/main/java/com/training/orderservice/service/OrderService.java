package com.training.orderservice.service;


import com.training.common.dto.OrderDTO;
import com.training.common.enums.OrderStatus;
import com.training.common.event.order.OrderCreatedEvent;
import com.training.common.event.payment.RefundRequestedEvent;
import com.training.orderservice.dto.RefundRequestDTO;
import com.training.orderservice.entity.Orders;
import com.training.orderservice.entity.OrdersRepository;
import com.training.orderservice.exception.ResourceNotFoundException;
import com.training.orderservice.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final OrderMapper orderMapper;
    private final OrderEventProducer eventProducer;

    private static final String ORDER_EVENTS_TOPIC = "order-events";
    private static final String REFUND_EVENTS_TOPIC = "refund-events";

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for customer: {}, restaurant: {}", orderDTO.getCustomerId(), orderDTO.getRestaurantId());

        Orders order = orderMapper.toEntity(orderDTO);
        Orders savedOrder = ordersRepository.save(order);

        log.info("Order saved to database with ID: {}", savedOrder.getId());

        OrderDTO savedOrderDto = orderMapper.toDto(savedOrder);
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrderDto
        );
        eventProducer.sendOrderEvent(ORDER_EVENTS_TOPIC, savedOrder.getId(), event);
        log.info("OrderCreatedEvent published for order: {}", order.getId());

        return savedOrderDto;
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomerId(String customerId) {
        return ordersRepository.findByCustomerId(customerId)
            .stream()
            .map(orderMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return ordersRepository.findAll()
            .stream()
            .map(orderMapper::toDto)
            .toList();
    }

    /**
     * Initiates async refund by updating order status and publishing RefundRequestedEvent.
     */
    @Transactional
    public void initiateRefund(RefundRequestDTO request) {
        String orderId = request.getOrderId();
        log.info("Initiating refund for order: {}, reason: {}", orderId, request.getReason());

        // Update order status to REFUND_REQUESTED
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setStatus(OrderStatus.REFUND_REQUESTED);
        ordersRepository.save(order);
        log.info("Order {} status updated to REFUND_REQUESTED", orderId);

        // Publish RefundRequestedEvent to Kafka
        RefundRequestedEvent event = new RefundRequestedEvent(
                orderId,
                request.getReason(),
                request.getAmount()
        );

        eventProducer.sendOrderEvent(REFUND_EVENTS_TOPIC, orderId, event);
        log.info("RefundRequestedEvent published for order: {}", orderId);
    }

    // Statuses that cannot be refunded
    private static final Set<OrderStatus> NON_REFUNDABLE_STATUSES = Set.of(
            OrderStatus.ORDER_CANCELED,
            OrderStatus.REFUNDED,
            OrderStatus.REFUND_REQUESTED,
            OrderStatus.REFUND_FAILED
    );

    /**
     * Validates order exists and can be refunded, then initiates async refund.
     * @throws ResourceNotFoundException if order not found
     * @throws IllegalStateException if order cannot be refunded
     */
    @Transactional
    public void validateAndInitiateRefund(String orderId, String reason) {
        log.info("Validating and initiating refund for order: {}, reason: {}", orderId, reason);

        // Validate that order exists
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Validate that order can be refunded
        if (NON_REFUNDABLE_STATUSES.contains(order.getStatus())) {
            throw new IllegalStateException(
                    "Order cannot be refunded. Current status: " + order.getStatus());
        }

        // Update order status to REFUND_REQUESTED
        order.setStatus(OrderStatus.REFUND_REQUESTED);
        ordersRepository.save(order);
        log.info("Order {} status updated to REFUND_REQUESTED", orderId);

        // Publish RefundRequestedEvent to Kafka using the dedicated method
        eventProducer.publishRefundRequested(orderId, reason);
        log.info("RefundRequestedEvent published for order: {}", orderId);
    }

}

package com.training.deliveryservice.service;

import com.training.deliveryservice.dto.AssignCourierRequest;
import com.training.deliveryservice.dto.DeliveryDTO;
import com.training.deliveryservice.entity.Delivery;
import com.training.deliveryservice.entity.DeliveryStatus;
import com.training.deliveryservice.mapper.DeliveryMapper;
import com.training.deliveryservice.repo.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryEventProducer eventProducer;

    @Transactional(readOnly = true)
    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream()
                .map(deliveryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryById(String id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id));
        return deliveryMapper.toDto(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryByOrderId(String orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
        return deliveryMapper.toDto(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryDTO> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status)
                .stream()
                .map(deliveryMapper::toDto)
                .toList();
    }

    @Transactional
    public DeliveryDTO assignCourier(String deliveryId, AssignCourierRequest request) {
        log.info("Assigning courier to delivery: deliveryId={}, courierName={}",
                deliveryId, request.getCourierName());

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot assign courier. Delivery status is: " + delivery.getStatus());
        }

        String courierId = request.getCourierId() != null
                ? request.getCourierId()
                : "COURIER-" + UUID.randomUUID().toString().substring(0, 8);

        delivery.setCourierId(courierId);
        delivery.setCourierName(request.getCourierName());
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Courier assigned: deliveryId={}, courierId={}, courierName={}",
                deliveryId, courierId, request.getCourierName());

        // Publish event
        Integer estimatedMinutes = request.getEstimatedDeliveryMinutes() != null
                ? request.getEstimatedDeliveryMinutes()
                : 30; // default 30 minutes

        eventProducer.publishDeliveryAssigned(
                delivery.getOrderId(),
                courierId,
                request.getCourierName(),
                estimatedMinutes
        );

        return deliveryMapper.toDto(savedDelivery);
    }

    @Transactional
    public DeliveryDTO startDelivery(String deliveryId) {
        log.info("Starting delivery: deliveryId={}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Cannot start delivery. Current status is: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.IN_TRANSIT);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Delivery in transit: deliveryId={}, orderId={}",
                deliveryId, delivery.getOrderId());

        return deliveryMapper.toDto(savedDelivery);
    }

    @Transactional
    public DeliveryDTO completeDelivery(String deliveryId) {
        log.info("Completing delivery: deliveryId={}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.ASSIGNED &&
            delivery.getStatus() != DeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException(
                    "Cannot complete delivery. Current status is: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery completed: deliveryId={}, orderId={}, deliveredAt={}",
                deliveryId, delivery.getOrderId(), delivery.getDeliveredAt());

        // Publish event
        eventProducer.publishDeliveryCompleted(
                delivery.getOrderId(),
                delivery.getCourierId()
        );

        return deliveryMapper.toDto(savedDelivery);
    }

    @Transactional
    public DeliveryDTO cancelDelivery(String deliveryId, String reason) {
        log.info("Cancelling delivery: deliveryId={}, reason={}", deliveryId, reason);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
            throw new IllegalStateException("Delivery is already cancelled");
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setCancellationReason(reason);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery cancelled: deliveryId={}, orderId={}, reason={}",
                deliveryId, delivery.getOrderId(), reason);

        // Publish event
        eventProducer.publishDeliveryCancelled(
                delivery.getOrderId(),
                deliveryId,
                reason
        );

        return deliveryMapper.toDto(savedDelivery);
    }

}

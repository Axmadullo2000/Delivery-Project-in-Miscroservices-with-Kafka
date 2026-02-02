package com.training.deliveryservice.controller;

import com.training.deliveryservice.dto.AssignCourierRequest;
import com.training.deliveryservice.dto.DeliveryDTO;
import com.training.deliveryservice.entity.DeliveryStatus;
import com.training.deliveryservice.service.DeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries() {
        log.info("Received get all deliveries request");
        List<DeliveryDTO> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDeliveryById(@PathVariable String id) {
        log.info("Received get delivery by id request: {}", id);
        DeliveryDTO delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryDTO> getDeliveryByOrderId(@PathVariable String orderId) {
        log.info("Received get delivery by orderId request: {}", orderId);
        DeliveryDTO delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryDTO>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        log.info("Received get deliveries by status request: {}", status);
        List<DeliveryDTO> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<DeliveryDTO> assignCourier(
            @PathVariable String id,
            @Valid @RequestBody AssignCourierRequest request) {
        log.info("Received assign courier request: deliveryId={}, courierName={}",
                id, request.getCourierName());
        DeliveryDTO delivery = deliveryService.assignCourier(id, request);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<DeliveryDTO> startDelivery(@PathVariable String id) {
        log.info("Received start delivery request: deliveryId={}", id);
        DeliveryDTO delivery = deliveryService.startDelivery(id);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<DeliveryDTO> completeDelivery(@PathVariable String id) {
        log.info("Received complete delivery request: deliveryId={}", id);
        DeliveryDTO delivery = deliveryService.completeDelivery(id);
        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<DeliveryDTO> cancelDelivery(
            @PathVariable String id,
            @RequestParam(defaultValue = "Cancelled by user") String reason) {
        log.info("Received cancel delivery request: deliveryId={}, reason={}", id, reason);
        DeliveryDTO delivery = deliveryService.cancelDelivery(id, reason);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "delivery-service"
        ));
    }

}

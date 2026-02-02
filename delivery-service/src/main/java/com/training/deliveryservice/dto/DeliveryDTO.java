package com.training.deliveryservice.dto;

import com.training.deliveryservice.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private String id;
    private String orderId;
    private String customerId;
    private String deliveryAddress;
    private String courierName;
    private String courierId;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

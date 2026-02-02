package com.training.common.dto;

import com.training.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private String customerId;
    private String restaurantId;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String phoneNumber;
    private String orderId;
    private OrderStatus status;
}

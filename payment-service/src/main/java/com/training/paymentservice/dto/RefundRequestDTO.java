package com.training.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
    private String orderId;
    private String reason;
    private BigDecimal amount; // null means full refund
}

package com.training.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDTO {
    private String orderId;
    private String status; // PROCESSING, SUCCESS, FAILED
    private String message;
}

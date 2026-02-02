package com.training.paymentservice.dto;


import com.training.common.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDTO {
    private String orderId;
    private RefundStatus status;
    private String message;
}

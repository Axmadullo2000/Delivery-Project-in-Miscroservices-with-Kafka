package com.training.deliveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignCourierRequest {

    @NotBlank(message = "Courier name is required")
    private String courierName;

    private String courierId;

    private Integer estimatedDeliveryMinutes;
}

package com.training.restaurantservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDTO {
    private String id;
    private String name;
    private String address;
    private Boolean isOpen;
    private Integer averageCookingTimeMinutes;
}

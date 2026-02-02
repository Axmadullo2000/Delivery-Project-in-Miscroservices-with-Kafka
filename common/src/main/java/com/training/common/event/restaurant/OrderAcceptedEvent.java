package com.training.common.event.restaurant;


import com.training.common.enums.OrderStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.training.common.enums.OrderStatus.ORDER_ACCEPTED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderAcceptedEvent extends BaseEvent<OrderStatus> {
    private String restaurantId;
    private String status; // PREPARING
    private LocalDateTime estimatedTime;

    public OrderAcceptedEvent(String orderId, String restaurantId, LocalDateTime estimatedTime) {
        super(orderId, ORDER_ACCEPTED);
        this.restaurantId = restaurantId;
        this.status = "PREPARING";
        this.estimatedTime = estimatedTime;
    }

}

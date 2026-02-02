package com.training.common.event.restaurant;

import com.training.common.enums.OrderStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.OrderStatus.ORDER_REJECTED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderRejectedEvent extends BaseEvent<OrderStatus> {
    private String restaurantId;
    private String status; // REJECTED
    private String reason;

    public OrderRejectedEvent(String orderId, String restaurantId, String reason) {
        super(orderId, ORDER_REJECTED);
        this.restaurantId = restaurantId;
        this.status = "REJECTED";
        this.reason = reason;
    }
}

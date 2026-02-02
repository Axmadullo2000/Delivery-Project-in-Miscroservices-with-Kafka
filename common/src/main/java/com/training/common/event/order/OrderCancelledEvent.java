package com.training.common.event.order;


import com.training.common.enums.OrderStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.OrderStatus.ORDER_CANCELED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCancelledEvent extends BaseEvent<OrderStatus> {
    private String restaurantId;
    private String reason;
    private String cancelledBy; // CUSTOMER, RESTAURANT, SYSTEM

    public OrderCancelledEvent(String orderId, String reason) {
        super(orderId, ORDER_CANCELED);
        this.reason = reason;
        this.cancelledBy = "SYSTEM";
    }

    public OrderCancelledEvent(String orderId, String restaurantId, String reason, String cancelledBy) {
        super(orderId, ORDER_CANCELED);
        this.restaurantId = restaurantId;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
    }

}

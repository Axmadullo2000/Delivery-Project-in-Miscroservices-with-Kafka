package com.training.common.event.delivery;

import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.RESTAURANT_ORDER_CANCELLED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestaurantOrderCancelledEvent extends BaseEvent<DeliveryStatus> {
    private String reason;

    public RestaurantOrderCancelledEvent(String orderId, String reason) {
        super(orderId, RESTAURANT_ORDER_CANCELLED);
        this.reason = reason;
    }

}

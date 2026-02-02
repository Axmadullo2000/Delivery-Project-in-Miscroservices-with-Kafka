package com.training.common.event.delivery;

import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.DELIVERY_COMPLETED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryCompletedEvent extends BaseEvent<DeliveryStatus> {
    private String courierId;

    public DeliveryCompletedEvent(String orderId, String courierId) {
        super(orderId, DELIVERY_COMPLETED);
        this.courierId = courierId;
    }

}

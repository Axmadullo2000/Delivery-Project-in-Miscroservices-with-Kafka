package com.training.common.event.delivery;

import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.DELIVERY_CREATED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryCreatedEvent extends BaseEvent<DeliveryStatus> {
    private String deliveryId;
    private String customerId;
    private String deliveryAddress;

    public DeliveryCreatedEvent(String orderId, String deliveryId, String customerId, String deliveryAddress) {
        super(orderId, DELIVERY_CREATED);
        this.deliveryId = deliveryId;
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
    }

}

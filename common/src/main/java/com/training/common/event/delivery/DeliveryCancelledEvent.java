package com.training.common.event.delivery;

import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.DELIVERY_CANCELLED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryCancelledEvent extends BaseEvent<DeliveryStatus> {
    private String deliveryId;
    private String reason;
    private String cancelledBy; // CUSTOMER, SYSTEM, COURIER

    public DeliveryCancelledEvent(String orderId, String deliveryId, String reason) {
        super(orderId, DELIVERY_CANCELLED);
        this.deliveryId = deliveryId;
        this.reason = reason;
        this.cancelledBy = "SYSTEM";
    }

    public DeliveryCancelledEvent(String orderId, String deliveryId, String reason, String cancelledBy) {
        super(orderId, DELIVERY_CANCELLED);
        this.deliveryId = deliveryId;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
    }

}

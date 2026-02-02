package com.training.common.event.delivery;

import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.DELIVERY_UNAVAILABLE;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryUnavailableEvent extends BaseEvent<DeliveryStatus> {
    private String reason;

    public DeliveryUnavailableEvent(String orderId, String reason) {
        super(orderId, DELIVERY_UNAVAILABLE);
        this.reason = reason;
    }

}

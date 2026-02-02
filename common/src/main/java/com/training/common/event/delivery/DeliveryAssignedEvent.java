package com.training.common.event.delivery;


import com.training.common.enums.DeliveryStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.DeliveryStatus.DELIVERY_ASSIGNED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryAssignedEvent extends BaseEvent<DeliveryStatus> {
    private String courierId;
    private String courierName;
    private Integer estimatedDeliveryTimes;

    public DeliveryAssignedEvent(String orderId,
                                 String courierId,
                                 String courierName,
                                 Integer estimatedDeliveryTimes
    ) {
        super(orderId, DELIVERY_ASSIGNED);

        this.courierId = courierId;
        this.courierName = courierName;
        this.estimatedDeliveryTimes = estimatedDeliveryTimes;

    }
}

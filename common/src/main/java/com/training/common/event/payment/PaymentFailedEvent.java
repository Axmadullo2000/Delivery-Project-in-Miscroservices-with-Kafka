package com.training.common.event.payment;


import com.training.common.enums.PaymentStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.PaymentStatus.PAYMENT_FAILED;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PaymentFailedEvent extends BaseEvent<PaymentStatus> {
    private String reason;

    public PaymentFailedEvent(String orderId, String reason) {
        super(orderId, PAYMENT_FAILED);
        this.reason = reason;
    }
}

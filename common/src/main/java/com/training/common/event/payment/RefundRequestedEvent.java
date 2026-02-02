package com.training.common.event.payment;

import com.training.common.enums.PaymentStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.training.common.enums.PaymentStatus.REFUND_REQUESTED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundRequestedEvent extends BaseEvent<PaymentStatus> {
    private String reason;
    private BigDecimal amount; // optional, null means full refund

    public RefundRequestedEvent(String orderId, String reason) {
        super(orderId, REFUND_REQUESTED);
        this.reason = reason;
        this.amount = null; // full refund
    }

    public RefundRequestedEvent(String orderId, String reason, BigDecimal amount) {
        super(orderId, REFUND_REQUESTED);
        this.reason = reason;
        this.amount = amount;
    }
}

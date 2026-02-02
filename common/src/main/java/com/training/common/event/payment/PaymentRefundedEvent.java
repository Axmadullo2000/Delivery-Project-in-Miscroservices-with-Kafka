package com.training.common.event.payment;

import com.training.common.enums.PaymentStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.training.common.enums.PaymentStatus.PAYMENT_REFUNDED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentRefundedEvent extends BaseEvent<PaymentStatus> {
    private BigDecimal amount;
    private String transactionId;
    private String reason;

    public PaymentRefundedEvent(String orderId, BigDecimal amount,
                                String transactionId, String reason) {
        super(orderId, PAYMENT_REFUNDED);
        this.amount = amount;
        this.transactionId = transactionId;
        this.reason = reason;
    }
}

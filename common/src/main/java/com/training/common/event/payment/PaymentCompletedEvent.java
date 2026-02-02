package com.training.common.event.payment;


import com.training.common.enums.PaymentStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.training.common.enums.PaymentStatus.PAYMENT_COMPLETED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent<PaymentStatus> {
    private BigDecimal amount;
    private String transactionId;

    public PaymentCompletedEvent(String orderId, BigDecimal amount, String transactionId) {
        super(orderId, PAYMENT_COMPLETED);
        this.amount = amount;
        this.transactionId = transactionId;
    }

}

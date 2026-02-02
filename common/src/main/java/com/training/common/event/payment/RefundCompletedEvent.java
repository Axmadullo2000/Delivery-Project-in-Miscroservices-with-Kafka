package com.training.common.event.payment;

import com.training.common.enums.PaymentStatus;
import com.training.common.enums.RefundStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.training.common.enums.PaymentStatus.REFUND_COMPLETED;
import static com.training.common.enums.RefundStatus.FAILED;
import static com.training.common.enums.RefundStatus.SUCCESS;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundCompletedEvent extends BaseEvent<PaymentStatus> {
    private BigDecimal amount;
    private String transactionId;
    private String refundTransactionId;
    private RefundStatus status; // SUCCESS, FAILED
    private String failureReason;

    // Success constructor
    public RefundCompletedEvent(String orderId, BigDecimal amount,
                                String transactionId, String refundTransactionId) {
        super(orderId, REFUND_COMPLETED);
        this.amount = amount;
        this.transactionId = transactionId;
        this.refundTransactionId = refundTransactionId;
        this.status = SUCCESS;
        this.failureReason = null;
    }

    // Failure constructor
    public RefundCompletedEvent(String orderId, String failureReason) {
        super(orderId, REFUND_COMPLETED);
        this.status = FAILED;
        this.failureReason = failureReason;
    }
}

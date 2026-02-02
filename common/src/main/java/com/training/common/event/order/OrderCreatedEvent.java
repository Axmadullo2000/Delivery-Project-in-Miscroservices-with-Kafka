package com.training.common.event.order;

import com.training.common.dto.OrderDTO;
import com.training.common.enums.OrderStatus;
import com.training.common.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.training.common.enums.OrderStatus.ORDER_CREATED;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent<OrderStatus> {
    private OrderDTO order;

    public OrderCreatedEvent(String orderId, OrderDTO order) {
        super(orderId, ORDER_CREATED);
        this.order = order;
    }

}

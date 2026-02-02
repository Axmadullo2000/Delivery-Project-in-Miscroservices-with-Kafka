package com.training.common.event;

import com.training.common.enums.EventType;
import com.training.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent<T extends EventType> {
    private String eventId = UUID.randomUUID().toString();
    private LocalDateTime timestamp = LocalDateTime.now();

    private String orderId;
    private T eventType;

    public BaseEvent(String orderId, T eventType) {
        this.orderId = orderId;
        this.eventType = eventType;
    }

}

package com.training.orderservice.mapper;


import com.training.common.dto.OrderDTO;
import com.training.common.dto.OrderItemDTO;
import com.training.common.enums.OrderStatus;

import com.training.orderservice.entity.OrderItem;
import com.training.orderservice.entity.Orders;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "status", source = "status")
    OrderDTO toDto(Orders orders);

    @AfterMapping
    default void setStatus(@MappingTarget OrderDTO dto, Orders orders) {
        if (orders.getStatus() != null) {
            dto.setStatus(OrderStatus.valueOf(orders.getStatus().name()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Orders toEntity(OrderDTO orderDTO);

    OrderItemDTO toItemDTO(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    OrderItem toItemEntity(OrderItemDTO dto);

}

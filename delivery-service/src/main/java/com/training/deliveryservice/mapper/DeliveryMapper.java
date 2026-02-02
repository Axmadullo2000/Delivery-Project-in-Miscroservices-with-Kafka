package com.training.deliveryservice.mapper;

import com.training.deliveryservice.dto.DeliveryDTO;
import com.training.deliveryservice.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeliveryMapper {

    DeliveryDTO toDto(Delivery delivery);

    Delivery toEntity(DeliveryDTO dto);
}

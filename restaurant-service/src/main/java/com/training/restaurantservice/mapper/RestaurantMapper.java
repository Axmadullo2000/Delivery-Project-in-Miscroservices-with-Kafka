package com.training.restaurantservice.mapper;

import com.training.restaurantservice.dto.RestaurantDTO;
import com.training.restaurantservice.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    RestaurantDTO toDto(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Restaurant toEntity(RestaurantDTO restaurantDTO);

}

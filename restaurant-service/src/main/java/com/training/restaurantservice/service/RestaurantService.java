package com.training.restaurantservice.service;

import com.training.restaurantservice.dto.RestaurantDTO;
import com.training.restaurantservice.entity.Restaurant;
import com.training.restaurantservice.repo.RestaurantRepository;
import com.training.restaurantservice.mapper.RestaurantMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Transactional
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO) {
        log.info("Creating restaurant: {}", restaurantDTO.getName());

        Restaurant restaurant = restaurantMapper.toEntity(restaurantDTO);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant saved to database with ID: {}", savedRestaurant.getId());

        return restaurantMapper.toDto(savedRestaurant);
    }

    @Transactional(readOnly = true)
    public RestaurantDTO getRestaurantById(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + restaurantId));
        return restaurantMapper.toDto(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RestaurantDTO> getOpenRestaurants() {
        return restaurantRepository.findByIsOpenTrue()
                .stream()
                .map(restaurantMapper::toDto)
                .toList();
    }

    @Transactional
    public RestaurantDTO updateRestaurantStatus(String restaurantId, Boolean isOpen) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + restaurantId));

        restaurant.setIsOpen(isOpen);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant {} status updated to: {}", restaurantId, isOpen ? "OPEN" : "CLOSED");

        return restaurantMapper.toDto(updatedRestaurant);
    }

}

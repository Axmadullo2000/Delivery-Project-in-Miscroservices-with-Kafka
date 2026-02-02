package com.training.restaurantservice.controller;

import com.training.restaurantservice.dto.RestaurantDTO;
import com.training.restaurantservice.service.RestaurantService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@Valid @RequestBody RestaurantDTO restaurantDTO) {
        log.info("Received create restaurant request: {}", restaurantDTO);
        RestaurantDTO createdRestaurant = restaurantService.createRestaurant(restaurantDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRestaurant);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
        log.info("Received get restaurant request: {}", restaurantId);
        RestaurantDTO restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        log.info("Received get all restaurants request");
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/open")
    public ResponseEntity<List<RestaurantDTO>> getOpenRestaurants() {
        log.info("Received get open restaurants request");
        List<RestaurantDTO> restaurants = restaurantService.getOpenRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PatchMapping("/{restaurantId}/status")
    public ResponseEntity<RestaurantDTO> updateRestaurantStatus(
            @PathVariable String restaurantId,
            @RequestParam Boolean isOpen) {
        log.info("Received update restaurant status request: {} -> {}", restaurantId, isOpen);
        RestaurantDTO restaurant = restaurantService.updateRestaurantStatus(restaurantId, isOpen);
        return ResponseEntity.ok(restaurant);
    }

}

package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.dto.RestaurantRequest;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .ownerId(request.getOwnerId())
                .isOpen(true) // Default to open when created
                .build();

        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}
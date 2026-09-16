package com.fooddelivery.order_service.client;

import org.springframework.stereotype.Component;

import com.fooddelivery.order_service.dto.MenuItemDto;
import com.fooddelivery.order_service.dto.RestaurantDto;
import com.fooddelivery.order_service.exception.RestaurantUnavailableException;

@Component
public class RestaurantClientFallback implements RestaurantClient {

    @Override
    public MenuItemDto getMenuItemById(Long id) {
        throw new RestaurantUnavailableException("restaurant-service is currently unavailable - please try again shortly");
    }

    @Override
    public RestaurantDto getRestaurantById(Long id) {
        throw new RestaurantUnavailableException("restaurant-service is currently unavailable - please try again shortly");
    }
}

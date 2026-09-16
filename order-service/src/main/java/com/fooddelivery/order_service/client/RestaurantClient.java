package com.fooddelivery.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fooddelivery.order_service.dto.MenuItemDto;
import com.fooddelivery.order_service.dto.RestaurantDto;

// Connect directly to the restaurant service (assuming it runs on port 8081)
// fallback + feign.circuitbreaker.enabled (application.properties) mean a down/slow
// restaurant-service degrades to RestaurantClientFallback instead of hanging the caller.
@FeignClient(name = "restaurant-service", url = "http://localhost:8081", fallback = RestaurantClientFallback.class)
public interface RestaurantClient {

    @GetMapping("/api/menu-items/{id}")
    MenuItemDto getMenuItemById(@PathVariable("id") Long id);

    @GetMapping("/api/restaurants/{id}")
    RestaurantDto getRestaurantById(@PathVariable("id") Long id);
}

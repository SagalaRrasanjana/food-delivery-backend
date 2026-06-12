package com.fooddelivery.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fooddelivery.order_service.dto.MenuItemDto;

// Connect directly to the restaurant service (assuming it runs on port 8081)
@FeignClient(name = "restaurant-service", url = "http://localhost:8081")
public interface RestaurantClient {

    @GetMapping("/api/menu-items/{id}")
    MenuItemDto getMenuItemById(@PathVariable("id") Long id);
}
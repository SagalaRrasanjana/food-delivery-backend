package com.fooddelivery.restaurant_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant_service.dto.MenuItemRequest;
import com.fooddelivery.restaurant_service.model.MenuItem;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.MenuItemRepository;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository; // id to look up the restaurant

    public MenuItem addMenuItem(MenuItemRequest request) {
        // 1. Find the restaurant first. If someone passes a bad ID, crash safely.
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + request.getRestaurantId()));

        // 2. Build the new menu item
        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .menuCategory(request.getMenuCategory())
                .isAvailable(true) // Default to available
                .restaurant(restaurant) // Link the actual restaurant entity!
                .build();

        // 3. Save to database
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }
}
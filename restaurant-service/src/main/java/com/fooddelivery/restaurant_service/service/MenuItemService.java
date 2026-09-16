package com.fooddelivery.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant_service.dto.AvailabilityRequest;
import com.fooddelivery.restaurant_service.dto.MenuItemRequest;
import com.fooddelivery.restaurant_service.dto.MenuItemUpdateRequest;
import com.fooddelivery.restaurant_service.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant_service.model.MenuItem;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.MenuItemRepository;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import com.fooddelivery.restaurant_service.security.JwtUserDetails;
import com.fooddelivery.restaurant_service.specification.MenuItemSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository; // id to look up the restaurant

    public MenuItem addMenuItem(MenuItemRequest request, JwtUserDetails caller) {
        // 1. Find the restaurant first. If someone passes a bad ID, 404 instead of crashing.
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + request.getRestaurantId()));

        // 2. Only that restaurant's owner (or an admin) can add items to its menu
        RestaurantService.assertOwnerOrAdmin(restaurant.getOwnerId(), caller);

        // 3. Build the new menu item
        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .menuCategory(request.getMenuCategory())
                .imageUrl(request.getImageUrl())
                .isAvailable(true) // Default to available
                .restaurant(restaurant) // Link the actual restaurant entity!
                .build();

        // 4. Save to database
        return menuItemRepository.save(menuItem);
    }

    public Page<MenuItem> getMenuByRestaurant(Long restaurantId, String category, Boolean available, Pageable pageable) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findAll(MenuItemSpecifications.build(restaurantId, category, available), pageable);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    public MenuItem updateMenuItem(Long id, MenuItemUpdateRequest request, JwtUserDetails caller) {
        MenuItem menuItem = getMenuItemById(id);
        RestaurantService.assertOwnerOrAdmin(menuItem.getRestaurant().getOwnerId(), caller);

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setMenuCategory(request.getMenuCategory());
        menuItem.setImageUrl(request.getImageUrl());

        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateAvailability(Long id, AvailabilityRequest request, JwtUserDetails caller) {
        MenuItem menuItem = getMenuItemById(id);
        RestaurantService.assertOwnerOrAdmin(menuItem.getRestaurant().getOwnerId(), caller);

        menuItem.setAvailable(request.getAvailable());
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id, JwtUserDetails caller) {
        MenuItem menuItem = getMenuItemById(id);
        RestaurantService.assertOwnerOrAdmin(menuItem.getRestaurant().getOwnerId(), caller);
        menuItemRepository.delete(menuItem);
    }
}

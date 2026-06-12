package com.fooddelivery.restaurant_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurant_service.dto.MenuItemRequest;
import com.fooddelivery.restaurant_service.model.MenuItem;
import com.fooddelivery.restaurant_service.service.MenuItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.addMenuItem(request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getMenuByRestaurant(restaurantId));
    }
    
    //  fetch a single item by its ID for the Order Service to use
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id)); 
    }
}
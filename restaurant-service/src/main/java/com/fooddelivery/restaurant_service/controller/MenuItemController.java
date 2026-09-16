package com.fooddelivery.restaurant_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurant_service.dto.AvailabilityRequest;
import com.fooddelivery.restaurant_service.dto.MenuItemRequest;
import com.fooddelivery.restaurant_service.dto.MenuItemUpdateRequest;
import com.fooddelivery.restaurant_service.model.MenuItem;
import com.fooddelivery.restaurant_service.security.JwtUserDetails;
import com.fooddelivery.restaurant_service.service.MenuItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@Valid @RequestBody MenuItemRequest request, Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(menuItemService.addMenuItem(request, caller));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Page<MenuItem>> getMenuByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(menuItemService.getMenuByRestaurant(restaurantId, category, available, pageable));
    }

    //  fetch a single item by its ID for the Order Service to use
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id));
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemUpdateRequest request,
            Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, request, caller));
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<MenuItem> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequest request,
            Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(menuItemService.updateAvailability(id, request, caller));
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id, Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        menuItemService.deleteMenuItem(id, caller);
        return ResponseEntity.noContent().build();
    }
}

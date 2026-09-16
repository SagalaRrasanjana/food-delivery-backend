package com.fooddelivery.restaurant_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurant_service.dto.RestaurantRequest;
import com.fooddelivery.restaurant_service.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.security.JwtUserDetails;
import com.fooddelivery.restaurant_service.service.RestaurantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Only restaurant owners/admins may open a new restaurant
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody RestaurantRequest request, Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(restaurantService.createRestaurant(request, caller.getUserId()));
    }

    // ownerId lets a restaurant owner's frontend resolve "my restaurant(s)"
    // without a dedicated endpoint — same public/permitAll list, just filtered.
    @GetMapping
    public ResponseEntity<Page<Restaurant>> getAllRestaurants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean isOpen,
            @RequestParam(required = false) Long ownerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(search, tag, isOpen, ownerId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    // Only the owning restaurant owner (or an admin) can edit/close a restaurant
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantUpdateRequest request,
            Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, request, caller));
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id, Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        restaurantService.deleteRestaurant(id, caller);
        return ResponseEntity.noContent().build();
    }
}

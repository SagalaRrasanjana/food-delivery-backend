package com.fooddelivery.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant_service.dto.RestaurantRequest;
import com.fooddelivery.restaurant_service.dto.RestaurantUpdateRequest;
import com.fooddelivery.restaurant_service.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import com.fooddelivery.restaurant_service.security.JwtUserDetails;
import com.fooddelivery.restaurant_service.specification.RestaurantSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(RestaurantRequest request, Long ownerId) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .ownerId(ownerId) // derived from the caller's JWT, never trusted from the client
                .bannerUrl(request.getBannerUrl())
                .tags(request.getTags())
                .isOpen(request.isOpen())
                .averageRating(0.0)
                .ratingCount(0)
                .build();

        return restaurantRepository.save(restaurant);
    }

    public Page<Restaurant> searchRestaurants(String search, String tag, Boolean open, Long ownerId, Pageable pageable) {
        return restaurantRepository.findAll(RestaurantSpecifications.build(search, tag, open, ownerId), pageable);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    public Restaurant updateRestaurant(Long id, RestaurantUpdateRequest request, JwtUserDetails caller) {
        Restaurant restaurant = getRestaurantById(id);
        assertOwnerOrAdmin(restaurant.getOwnerId(), caller);

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setBannerUrl(request.getBannerUrl());
        restaurant.setTags(request.getTags());
        restaurant.setOpen(request.isOpen());

        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id, JwtUserDetails caller) {
        Restaurant restaurant = getRestaurantById(id);
        assertOwnerOrAdmin(restaurant.getOwnerId(), caller);
        restaurantRepository.delete(restaurant);
    }

    public static void assertOwnerOrAdmin(Long resourceOwnerId, JwtUserDetails caller) {
        boolean isAdmin = caller != null && "ADMIN".equals(caller.getRole());
        boolean isOwner = caller != null && caller.getUserId() != null && caller.getUserId().equals(resourceOwnerId);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to modify this restaurant");
        }
    }
}

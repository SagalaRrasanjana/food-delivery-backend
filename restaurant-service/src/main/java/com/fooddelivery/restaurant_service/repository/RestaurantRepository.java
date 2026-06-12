package com.fooddelivery.restaurant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurant_service.model.Restaurant;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    List<Restaurant> findByOwnerId(Long ownerId);
}
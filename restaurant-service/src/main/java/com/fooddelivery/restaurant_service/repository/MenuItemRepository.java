package com.fooddelivery.restaurant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fooddelivery.restaurant_service.model.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    // Spring Data JPA automatically writes the SQL to find items by the foreign key!
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
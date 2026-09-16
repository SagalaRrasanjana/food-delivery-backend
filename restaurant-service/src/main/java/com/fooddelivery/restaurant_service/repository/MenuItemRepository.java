package com.fooddelivery.restaurant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fooddelivery.restaurant_service.model.MenuItem;


public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {

    // Spring Data JPA automatically writes the SQL to find items by the foreign key!
    List<MenuItem> findByRestaurantId(Long restaurantId);
}

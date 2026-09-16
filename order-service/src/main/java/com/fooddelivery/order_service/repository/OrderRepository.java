package com.fooddelivery.order_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.order_service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom method to fetch a user's entire order history
    List<Order> findByUserIdOrderByOrderTimeDesc(String userId);

    Page<Order> findByUserIdOrderByOrderTimeDesc(String userId, Pageable pageable);

    Page<Order> findByRestaurantIdOrderByOrderTimeDesc(Long restaurantId, Pageable pageable);
}

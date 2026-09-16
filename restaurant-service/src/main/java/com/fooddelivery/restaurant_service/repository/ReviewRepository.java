package com.fooddelivery.restaurant_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurant_service.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId, Pageable pageable);

    boolean existsByRestaurantIdAndUserEmail(Long restaurantId, String userEmail);
}

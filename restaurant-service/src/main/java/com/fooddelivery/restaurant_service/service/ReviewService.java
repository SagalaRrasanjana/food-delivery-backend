package com.fooddelivery.restaurant_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant_service.dto.ReviewRequest;
import com.fooddelivery.restaurant_service.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.model.Review;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import com.fooddelivery.restaurant_service.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    public Review addReview(Long restaurantId, ReviewRequest request, String userEmail) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        if (reviewRepository.existsByRestaurantIdAndUserEmail(restaurantId, userEmail)) {
            throw new IllegalStateException("You have already reviewed this restaurant");
        }

        Review review = Review.builder()
                .restaurantId(restaurantId)
                .userEmail(userEmail)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        review = reviewRepository.save(review);

        // Roll the new rating into the restaurant's running average
        double totalScore = restaurant.getAverageRating() * restaurant.getRatingCount() + request.getRating();
        int newCount = restaurant.getRatingCount() + 1;
        restaurant.setRatingCount(newCount);
        restaurant.setAverageRating(totalScore / newCount);
        restaurantRepository.save(restaurant);

        return review;
    }

    public Page<Review> getReviews(Long restaurantId, Pageable pageable) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId, pageable);
    }
}

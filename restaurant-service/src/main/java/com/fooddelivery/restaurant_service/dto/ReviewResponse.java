package com.fooddelivery.restaurant_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long restaurantId;
    private String userEmail;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewResponse from(com.fooddelivery.restaurant_service.model.Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRestaurantId(),
                review.getUserEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }
}

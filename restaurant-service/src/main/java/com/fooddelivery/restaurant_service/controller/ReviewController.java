package com.fooddelivery.restaurant_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurant_service.dto.ReviewRequest;
import com.fooddelivery.restaurant_service.dto.ReviewResponse;
import com.fooddelivery.restaurant_service.model.Review;
import com.fooddelivery.restaurant_service.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Any authenticated user can leave a review (SecurityConfig only permitAll()s GETs)
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Long restaurantId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        Review review = reviewService.addReview(restaurantId, request, authentication.getName());
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviews(restaurantId, pageable).map(ReviewResponse::from));
    }
}

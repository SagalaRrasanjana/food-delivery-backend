package com.fooddelivery.order_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.order_service.dto.OrderEstimateResponse;
import com.fooddelivery.order_service.dto.OrderRequest;
import com.fooddelivery.order_service.dto.StatusUpdateRequest;
import com.fooddelivery.order_service.model.Order;
import com.fooddelivery.order_service.security.JwtUserDetails;
import com.fooddelivery.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest orderRequest, Authentication authentication) {

        // 🚀 Spring Security automatically pulls the real User ID/Email from your verified JWT token!
        String userId = authentication.getName();

        return ResponseEntity.ok(orderService.placeOrder(orderRequest, userId));
    }

    // Prices a cart without creating an order - lets the frontend show a checkout total
    @PostMapping("/estimate")
    public ResponseEntity<OrderEstimateResponse> estimateOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.estimateOrder(orderRequest));
    }

    // Restaurant owner / admin only - moves an order through CONFIRMED -> ... -> DELIVERED
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody StatusUpdateRequest request,
            Authentication authentication) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus(), caller));
    }

    // Customer-initiated cancellation, only while the order is still PENDING
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, authentication.getName()));
    }

    // 👇 THIS IS THE SECURE HISTORY ENDPOINT!
    @GetMapping("/history")
    public ResponseEntity<Page<Order>> getMyOrderHistory(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {

        // 1. Securely grab the email from the verified token
        String userId = authentication.getName();

        // 2. Fetch only their specific orders (sorted newest to oldest by the repository!)
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    // Restaurant-facing view of incoming orders for a given restaurant
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Page<Order>> getRestaurantOrders(
            @PathVariable Long restaurantId,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        JwtUserDetails caller = (JwtUserDetails) authentication.getDetails();
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, caller, pageable));
    }
}

package com.fooddelivery.order_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.order_service.dto.OrderRequest;
import com.fooddelivery.order_service.model.Order;
import com.fooddelivery.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<Order> placeOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String token) {
        
        // Temporarily hardcoding the userId until we add the JWT filter in the next step
        String userId = "user123"; 
        return ResponseEntity.ok(orderService.placeOrder(orderRequest, userId));
    }

    // Get all orders for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        // We will call a method from your repository here later
        return ResponseEntity.ok(List.of()); 
    }
}
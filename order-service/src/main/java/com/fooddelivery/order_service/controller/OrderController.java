package com.fooddelivery.order_service.controller;

import java.util.List;
import java.util.Map; 

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
        
        // 🚀 Spring Security automatically pulls the real User ID/Email from your verified JWT token!
        String userId = authentication.getName(); 
        
        return ResponseEntity.ok(orderService.placeOrder(orderRequest, userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> requestBody) {
        
        // Extract the "status" value from the JSON body
        String newStatus = requestBody.get("status");
        
        // Send it to the service to be saved
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    // 👇 THIS IS THE NEW SECURE HISTORY ENDPOINT!
    @GetMapping("/history")
    public ResponseEntity<List<Order>> getMyOrderHistory(Authentication authentication) {
        
        // 1. Securely grab the email from the verified token
        String userId = authentication.getName(); 
        
        // 2. Fetch only their specific orders (sorted newest to oldest by the repository!)
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
}
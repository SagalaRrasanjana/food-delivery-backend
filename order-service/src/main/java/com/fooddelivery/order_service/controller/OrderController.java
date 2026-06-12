package com.fooddelivery.order_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // 🚨 MAKE SURE TO ADD THIS IMPORT
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(List.of()); 
    }
}
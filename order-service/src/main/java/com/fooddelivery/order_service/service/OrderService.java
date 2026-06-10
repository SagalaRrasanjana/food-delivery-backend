package com.fooddelivery.order_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fooddelivery.order_service.dto.OrderRequest;
import com.fooddelivery.order_service.model.Order;
import com.fooddelivery.order_service.model.OrderItem;
import com.fooddelivery.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order placeOrder(OrderRequest request, String userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(request.getRestaurantId());
        order.setStatus("PENDING"); // New orders start as PENDING
        order.setOrderTime(LocalDateTime.now());

        // Convert the DTOs into actual Database Entities
        List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {
            OrderItem item = new OrderItem();
            item.setMenuItemId(itemRequest.getMenuItemId());
            item.setName(itemRequest.getName());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(itemRequest.getPrice());
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // Calculate the total checkout price
        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        
        order.setTotalAmount(total);

        // Save and return the finalized order
        return orderRepository.save(order);
    }
}
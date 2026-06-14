package com.fooddelivery.order_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

// 👇 Added these two imports for the Feign bridge
import com.fooddelivery.order_service.client.RestaurantClient;
import com.fooddelivery.order_service.dto.MenuItemDto;
import com.fooddelivery.order_service.dto.OrderRequest;
import com.fooddelivery.order_service.model.Order;
import com.fooddelivery.order_service.model.OrderItem;
import com.fooddelivery.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    
    // 👇 1. Inject the Feign Client
    private final RestaurantClient restaurantClient; 

    public Order placeOrder(OrderRequest request, String userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(request.getRestaurantId());
        order.setStatus("PENDING"); 
        order.setOrderTime(LocalDateTime.now());

        // Convert the DTOs into actual Database Entities
        List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {
            
            // 👇 2. Ping port 8081 to get the exact, un-hackable truth about this item
            MenuItemDto actualItem = restaurantClient.getMenuItemById(itemRequest.getMenuItemId());

            // 👇 3. Validate that it exists and is not sold out
            if (actualItem == null) {
                throw new RuntimeException("Menu item not found for ID: " + itemRequest.getMenuItemId());
            }
            if (actualItem.getAvailable() != null && !actualItem.getAvailable()) {
                throw new RuntimeException("Sorry, '" + actualItem.getName() + "' is currently sold out!");
            }

            OrderItem item = new OrderItem();
            item.setMenuItemId(itemRequest.getMenuItemId());
            // 👇 4. Use the TRUSTED name and TRUSTED price from the database, not the user!
            item.setName(actualItem.getName()); 
            item.setPrice(actualItem.getPrice()); 
            item.setQuantity(itemRequest.getQuantity());
            
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

    public Order updateOrderStatus(Long orderId, String newStatus) {
        // 1. Find the exact order in the database
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // 2. Update the status (converting to uppercase to keep the database clean)
        order.setStatus(newStatus.toUpperCase());
        
        // 3. Save the updated order
        return orderRepository.save(order);
    }
        // fetch the history
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserIdOrderByOrderTimeDesc(userId);
    }
}
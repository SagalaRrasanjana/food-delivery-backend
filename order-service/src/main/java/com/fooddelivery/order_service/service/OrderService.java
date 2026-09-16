package com.fooddelivery.order_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.fooddelivery.order_service.client.RestaurantClient;
import com.fooddelivery.order_service.dto.MenuItemDto;
import com.fooddelivery.order_service.dto.OrderEstimateResponse;
import com.fooddelivery.order_service.dto.OrderItemEstimate;
import com.fooddelivery.order_service.dto.OrderRequest;
import com.fooddelivery.order_service.dto.RestaurantDto;
import com.fooddelivery.order_service.exception.ResourceNotFoundException;
import com.fooddelivery.order_service.model.Order;
import com.fooddelivery.order_service.model.OrderItem;
import com.fooddelivery.order_service.model.OrderStatus;
import com.fooddelivery.order_service.repository.OrderRepository;
import com.fooddelivery.order_service.security.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;

    @Value("${order.delivery-fee}")
    private double deliveryFee;

    public Order placeOrder(OrderRequest request, String userId) {
        List<OrderItem> orderItems = resolveItems(request);
        double subtotal = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(request.getRestaurantId());
        order.setItems(orderItems);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderTime(LocalDateTime.now());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryNotes(request.getDeliveryNotes());
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotalAmount(subtotal + deliveryFee);

        return orderRepository.save(order);
    }

    // Prices a cart without persisting anything - lets a frontend show a checkout total
    public OrderEstimateResponse estimateOrder(OrderRequest request) {
        List<OrderItem> orderItems = resolveItems(request);

        List<OrderItemEstimate> estimates = orderItems.stream()
                .map(item -> new OrderItemEstimate(
                        item.getMenuItemId(), item.getName(), item.getPrice(), item.getQuantity(),
                        item.getPrice() * item.getQuantity()))
                .collect(Collectors.toList());

        double subtotal = estimates.stream().mapToDouble(OrderItemEstimate::getLineTotal).sum();

        return new OrderEstimateResponse(request.getRestaurantId(), estimates, subtotal, deliveryFee, subtotal + deliveryFee);
    }

    private List<OrderItem> resolveItems(OrderRequest request) {
        return request.getItems().stream().map(itemRequest -> {
            // Ping restaurant-service to get the exact, un-hackable truth about this item
            MenuItemDto actualItem = restaurantClient.getMenuItemById(itemRequest.getMenuItemId());

            if (actualItem == null) {
                throw new ResourceNotFoundException("Menu item not found for ID: " + itemRequest.getMenuItemId());
            }
            if (actualItem.getAvailable() != null && !actualItem.getAvailable()) {
                throw new IllegalStateException("Sorry, '" + actualItem.getName() + "' is currently sold out!");
            }

            OrderItem item = new OrderItem();
            item.setMenuItemId(itemRequest.getMenuItemId());
            // Use the TRUSTED name and TRUSTED price from the database, not the user!
            item.setName(actualItem.getName());
            item.setPrice(actualItem.getPrice());
            item.setQuantity(itemRequest.getQuantity());

            return item;
        }).collect(Collectors.toList());
    }

    // Restaurant owner / admin transition: CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED (or CANCELLED)
    public Order updateOrderStatus(Long orderId, String newStatusRaw, JwtUserDetails caller) {
        Order order = getOrderOrThrow(orderId);

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusRaw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown order status: " + newStatusRaw);
        }

        assertCanManageOrder(order, caller);

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot move order from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // Customer-initiated cancellation - only while the restaurant hasn't started preparing it
    public Order cancelOrder(Long orderId, String callerEmail) {
        Order order = getOrderOrThrow(orderId);

        if (!order.getUserId().equals(callerEmail)) {
            throw new AccessDeniedException("You can only cancel your own orders");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order can no longer be cancelled (status: " + order.getStatus() + ")");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Page<Order> getUserOrders(String userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByOrderTimeDesc(userId, pageable);
    }

    // Restaurant-facing view of incoming orders for one restaurant
    public Page<Order> getRestaurantOrders(Long restaurantId, JwtUserDetails caller, Pageable pageable) {
        assertOwnsRestaurantOrIsAdmin(restaurantId, caller);
        return orderRepository.findByRestaurantIdOrderByOrderTimeDesc(restaurantId, pageable);
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
    }

    private void assertCanManageOrder(Order order, JwtUserDetails caller) {
        if (caller != null && "ADMIN".equals(caller.getRole())) {
            return;
        }
        assertOwnsRestaurantOrIsAdmin(order.getRestaurantId(), caller);
    }

    private void assertOwnsRestaurantOrIsAdmin(Long restaurantId, JwtUserDetails caller) {
        if (caller != null && "ADMIN".equals(caller.getRole())) {
            return;
        }

        RestaurantDto restaurant = restaurantClient.getRestaurantById(restaurantId);
        if (restaurant == null || caller == null || caller.getUserId() == null
                || !caller.getUserId().equals(restaurant.getOwnerId())) {
            throw new AccessDeniedException("You do not own this restaurant");
        }
    }
}

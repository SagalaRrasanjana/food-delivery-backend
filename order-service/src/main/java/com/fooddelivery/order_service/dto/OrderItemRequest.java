package com.fooddelivery.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull(message = "menuItemId is required")
    private Long menuItemId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    // name/price are never trusted from the client - OrderService always re-fetches
    // the authoritative values from restaurant-service before persisting an OrderItem.
    private String name;
    private Double price;
}

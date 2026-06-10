package com.fooddelivery.order_service.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long menuItemId;
    private String name;
    private Integer quantity;
    private Double price;
}
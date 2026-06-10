package com.fooddelivery.order_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private Long restaurantId;
    private List<OrderItemRequest> items;
}
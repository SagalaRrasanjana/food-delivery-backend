package com.fooddelivery.order_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderEstimateResponse {
    private Long restaurantId;
    private List<OrderItemEstimate> items;
    private double subtotal;
    private double deliveryFee;
    private double total;
}

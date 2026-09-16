package com.fooddelivery.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemEstimate {
    private Long menuItemId;
    private String name;
    private Double price;
    private Integer quantity;
    private Double lineTotal;
}

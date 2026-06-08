package com.fooddelivery.restaurant_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MenuItemRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String menuCategory;
    
    //  identify which restaurant food belongs
    private Long restaurantId; 
}
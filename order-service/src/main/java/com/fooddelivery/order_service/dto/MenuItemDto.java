package com.fooddelivery.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

// ignoreUnknown: restaurant-service's MenuItem has more fields (description, menuCategory,
// imageUrl, ...) than order-service needs - without this, Jackson's default
// FAIL_ON_UNKNOWN_PROPERTIES would break this Feign call whenever that entity grows.
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuItemDto {
    private Long id;
    private String name;
    private Double price;
    private Long restaurantId;
    private Boolean available; 
}
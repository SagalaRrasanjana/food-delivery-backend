package com.fooddelivery.restaurant_service.dto;

import lombok.Data;

@Data
public class RestaurantRequest {
    private String name;
    private String address;
    private Long ownerId; 
    private String bannerUrl;
    private String tags;
    private boolean isOpen;
}
package com.fooddelivery.restaurant_service.dto;

import lombok.Data;

@Data
public class RestaurantRequest {
    private String name;
    private String address;
    private Long ownerId; //  pass this manually - until link the Auth token
}
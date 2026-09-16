package com.fooddelivery.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDto {
    private Long id;
    private Long ownerId;
}

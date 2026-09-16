package com.fooddelivery.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Owner-editable fields only - ownerId is immutable after creation, not client-settable
@Data
public class RestaurantUpdateRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "address is required")
    private String address;

    private String bannerUrl;
    private String tags;
    private boolean isOpen;
}

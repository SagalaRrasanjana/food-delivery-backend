package com.fooddelivery.order_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "restaurantId is required")
    private Long restaurantId;

    @NotEmpty(message = "an order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "deliveryAddress is required")
    private String deliveryAddress;

    private String deliveryNotes;
}

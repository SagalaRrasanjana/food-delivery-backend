package com.fooddelivery.order_service.model;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    // Explicit state machine - replaces the old "any string, any transition" behavior
    public boolean canTransitionTo(OrderStatus target) {
        return allowedNextStates().contains(target);
    }

    public Set<OrderStatus> allowedNextStates() {
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(PREPARING, CANCELLED);
            case PREPARING -> EnumSet.of(OUT_FOR_DELIVERY, CANCELLED);
            case OUT_FOR_DELIVERY -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
    }
}

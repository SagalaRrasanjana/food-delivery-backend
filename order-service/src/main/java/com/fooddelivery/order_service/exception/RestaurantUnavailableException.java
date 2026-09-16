package com.fooddelivery.order_service.exception;

// Thrown by the Feign circuit-breaker fallback when restaurant-service is down/slow,
// so callers get a clean 503 instead of placeOrder hanging or throwing a raw Feign error.
public class RestaurantUnavailableException extends RuntimeException {
    public RestaurantUnavailableException(String message) {
        super(message);
    }
}

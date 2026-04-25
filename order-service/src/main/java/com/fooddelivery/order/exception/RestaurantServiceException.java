package com.fooddelivery.order.exception;

/**
 * CUSTOM EXCEPTION - Restaurant Service Communication Error
 *
 * Thrown when communication with Restaurant Service fails
 * Results in HTTP 503 SERVICE UNAVAILABLE response
 */
public class RestaurantServiceException extends RuntimeException {

    public RestaurantServiceException(String message) {
        super(message);
    }

    public RestaurantServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

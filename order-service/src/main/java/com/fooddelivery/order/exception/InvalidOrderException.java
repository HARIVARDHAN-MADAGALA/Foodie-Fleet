package com.fooddelivery.order.exception;

/**
 * CUSTOM EXCEPTION - Invalid Order Data
 *
 * Thrown when order data validation fails
 * Results in HTTP 400 BAD REQUEST response
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

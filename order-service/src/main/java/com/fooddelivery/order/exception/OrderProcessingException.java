package com.fooddelivery.order.exception;

/**
 * CUSTOM EXCEPTION - Order Processing Error
 *
 * Thrown when order processing fails due to business logic
 * Results in HTTP 400 BAD REQUEST response
 */
public class OrderProcessingException extends RuntimeException {

    private String errorCode;

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}

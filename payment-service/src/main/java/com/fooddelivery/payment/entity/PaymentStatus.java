package com.fooddelivery.payment.entity;

/**
 * PAYMENT STATUS
 *
 * Lifecycle of a payment transaction
 */
public enum PaymentStatus {
    PENDING,      // Payment initiated
    PROCESSING,   // Being processed by payment gateway
    SUCCESS,      // Payment successful
    FAILED,       // Payment failed
    REFUNDED      // Payment refunded
}

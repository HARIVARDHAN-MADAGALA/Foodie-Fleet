package com.fooddelivery.order.entity;

/**
 * ORDER STATUS ENUM
 * 
 * Represents different states of an order
 */
public enum OrderStatus {
    PLACED,       // Order placed by customer
    CONFIRMED,    // Restaurant confirmed the order
    PREPARING,    // Food is being prepared
    READY,        // Food is ready for pickup
    PICKED_UP,    // Delivery partner picked up the order
    DELIVERED,    // Order delivered to customer
    CANCELLED     // Order was cancelled
}

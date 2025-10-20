package com.fooddelivery.delivery.entity;

/**
 * DELIVERY STATUS
 *
 * Lifecycle of a delivery
 */
public enum DeliveryStatus {
    ASSIGNED,       // Partner assigned to delivery
    PICKED_UP,      // Partner picked up order from restaurant
    IN_TRANSIT,     // Partner on the way to customer
    DELIVERED,      // Successfully delivered
    FAILED,         // Delivery failed
    CANCELLED       // Delivery cancelled
}

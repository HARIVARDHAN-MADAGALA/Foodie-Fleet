package com.fooddelivery.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DELIVERY ENTITY
 *
 * Represents a delivery assignment in the delivery_db database.
 * Links orders to delivery partners.
 */
@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = DeliveryStatus.ASSIGNED;
        }
        assignedAt = LocalDateTime.now();
    }
}

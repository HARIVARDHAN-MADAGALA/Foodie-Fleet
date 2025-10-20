package com.fooddelivery.rating.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * RATING ENTITY
 *
 * Represents a user rating for an order.
 * Can rate both the restaurant and the delivery partner.
 */
@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;

    @Column(name = "restaurant_rating", nullable = false)
    private Integer restaurantRating; // 1-5 stars

    @Column(name = "delivery_rating")
    private Integer deliveryRating; // 1-5 stars

    @Column(name = "food_quality")
    private Integer foodQuality; // 1-5 stars

    @Column(name = "delivery_speed")
    private Integer deliverySpeed; // 1-5 stars

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

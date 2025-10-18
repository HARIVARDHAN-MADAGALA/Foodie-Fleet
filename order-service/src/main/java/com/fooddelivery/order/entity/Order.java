package com.fooddelivery.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ORDER ENTITY
 * 
 * Represents a food order in the system.
 * Tracks order status from placement to delivery.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long restaurantId;

    private Long deliveryPartnerId;

    private Long addressId;

    @Column(nullable = false)
    private Double totalAmount;

    private Double deliveryFee = 0.0;

    private Double discount = 0.0;

    private Double finalAmount;

    /**
     * ORDER STATUS FLOW:
     * PLACED → CONFIRMED → PREPARING → READY → PICKED_UP → DELIVERED
     * Can be CANCELLED at any stage before PICKED_UP
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String specialInstructions;

    @Column(nullable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    private LocalDateTime deliveryTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}

package com.fooddelivery.order.event;

import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ORDER EVENT - Published to Kafka
 * 
 * This event is sent to Kafka topics when:
 * - New order is placed
 * - Order status changes
 * - Payment status changes
 * 
 * Other services (Payment, Delivery, Notification) consume these events
 * and react accordingly.
 * 
 * This demonstrates EVENT-DRIVEN ARCHITECTURE:
 * Services don't call each other directly. Instead, they publish events
 * and subscribe to events they're interested in.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Long deliveryPartnerId;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String eventType;  // ORDER_CREATED, ORDER_UPDATED, PAYMENT_COMPLETED, etc.
    private LocalDateTime timestamp;

    public OrderEvent(Long orderId, String eventType) {
        this.orderId = orderId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
}

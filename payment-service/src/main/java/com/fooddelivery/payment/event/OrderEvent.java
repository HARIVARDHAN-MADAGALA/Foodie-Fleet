package com.fooddelivery.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ORDER EVENT - Shared Event DTO
 *
 * This class is duplicated across services (microservices pattern).
 * Each service owns its copy to maintain independence.
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
    private String orderStatus;
    private String eventType;  // ORDER_CREATED, PAYMENT_COMPLETED, etc.
    private LocalDateTime timestamp;
}

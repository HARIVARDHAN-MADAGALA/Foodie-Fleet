package com.fooddelivery.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DELIVERY DTO - Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {

    private Long id;
    private Long orderId;
    private Long partnerId;
    private Long restaurantId;
    private String pickupAddress;
    private String deliveryAddress;
    private String status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryTime;
    private String notes;
}

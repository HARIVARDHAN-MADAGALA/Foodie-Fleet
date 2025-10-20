package com.fooddelivery.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * PAYMENT DTO - Data Transfer Object
 *
 * Used for API requests and responses.
 * Keeps the entity layer separate from the API layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long id;
    private Long orderId;
    private Long userId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String gatewayResponse;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

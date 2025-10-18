package com.fooddelivery.order.dto;

import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.PaymentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    private Long deliveryPartnerId;

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items;

    private Double totalAmount;
    private Double deliveryFee;
    private Double discount;
    private Double finalAmount;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String specialInstructions;
    private LocalDateTime orderTime;
    private LocalDateTime deliveryTime;
}

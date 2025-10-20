package com.fooddelivery.payment.dto;

import com.fooddelivery.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    @NotNull
    private Long orderId;
    @NotNull
    private Double amount;
    @NotNull
    private PaymentMethod paymentMethod;
}
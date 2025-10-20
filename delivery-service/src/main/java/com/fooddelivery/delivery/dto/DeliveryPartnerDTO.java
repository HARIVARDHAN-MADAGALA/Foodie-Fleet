package com.fooddelivery.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DELIVERY PARTNER DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartnerDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private String status;
    private Boolean isAvailable;
    private Double rating;
    private Integer totalDeliveries;
}

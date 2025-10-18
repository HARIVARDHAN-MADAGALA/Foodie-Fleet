package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RESTAURANT DATA TRANSFER OBJECT (DTO)
 * 
 * Purpose: Used to transfer restaurant data between client and server.
 * 
 * Why use DTOs?
 * 1. Separates internal entity structure from API responses
 * 2. Validates input data before processing
 * 3. Controls which data is exposed to clients
 * 4. Prevents over-posting and under-posting issues
 * 
 * Validation Annotations:
 * @NotBlank: Field cannot be null or empty
 * @Email: Validates email format
 * @Pattern: Validates against regex pattern
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {

    private Long id;

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Cuisine type is required")
    private String cuisine;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private Boolean isActive;

    private Double rating;

    private Integer totalRatings;
}

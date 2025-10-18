package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MENU ITEM DATA TRANSFER OBJECT
 * 
 * Used for creating and updating menu items.
 * Separates API input/output from database entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {

    private Long id;

    @NotBlank(message = "Menu item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private String category;

    private Boolean isAvailable;

    private String imageUrl;

    private Boolean isVegetarian;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}

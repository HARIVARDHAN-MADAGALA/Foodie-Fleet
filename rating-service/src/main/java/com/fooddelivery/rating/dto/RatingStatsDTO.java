package com.fooddelivery.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RATING STATISTICS DTO
 *
 * Contains aggregate rating data for restaurants or delivery partners
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatsDTO {

    private Long entityId;           // Restaurant ID or Partner ID
    private String entityType;       // "RESTAURANT" or "DELIVERY_PARTNER"
    private Double averageRating;
    private Long totalRatings;
}

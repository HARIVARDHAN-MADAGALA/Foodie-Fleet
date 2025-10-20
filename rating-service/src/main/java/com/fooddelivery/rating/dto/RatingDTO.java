package com.fooddelivery.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * RATING DTO - Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    private Long id;
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Long deliveryPartnerId;
    private Integer restaurantRating;  // 1-5 stars
    private Integer deliveryRating;    // 1-5 stars
    private Integer foodQuality;       // 1-5 stars
    private Integer deliverySpeed;     // 1-5 stars
    private String comment;
    private LocalDateTime createdAt;
}

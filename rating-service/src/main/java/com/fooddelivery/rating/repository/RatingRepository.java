package com.fooddelivery.rating.repository;

import com.fooddelivery.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RATING REPOSITORY
 *
 * Data access layer for Rating entity
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Find rating by order ID
     */
    Optional<Rating> findByOrderId(Long orderId);

    /**
     * Find all ratings by user
     */
    List<Rating> findByUserId(Long userId);

    /**
     * Find all ratings for a restaurant
     */
    List<Rating> findByRestaurantId(Long restaurantId);

    /**
     * Find all ratings for a delivery partner
     */
    List<Rating> findByDeliveryPartnerId(Long deliveryPartnerId);

    /**
     * Calculate average restaurant rating
     */
    @Query("SELECT AVG(r.restaurantRating) FROM Rating r WHERE r.restaurantId = :restaurantId")
    Double calculateAverageRestaurantRating(@Param("restaurantId") Long restaurantId);

    /**
     * Calculate average delivery partner rating
     */
    @Query("SELECT AVG(r.deliveryRating) FROM Rating r WHERE r.deliveryPartnerId = :partnerId")
    Double calculateAverageDeliveryRating(@Param("partnerId") Long partnerId);

    /**
     * Check if rating exists for order
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Count total ratings for restaurant
     */
    long countByRestaurantId(Long restaurantId);
}

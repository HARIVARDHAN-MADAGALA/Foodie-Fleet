package com.fooddelivery.rating.service;

import com.fooddelivery.rating.dto.RatingDTO;
import com.fooddelivery.rating.dto.RatingStatsDTO;
import com.fooddelivery.rating.entity.Rating;
import com.fooddelivery.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RATING SERVICE - Business Logic
 *
 * Handles rating and review operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * CREATE RATING
     *
     * Users can rate after order is delivered
     */
    @Transactional
    public RatingDTO createRating(RatingDTO ratingDTO) {
        log.info("⭐ Creating rating for Order ID: {}", ratingDTO.getOrderId());

        // Check if rating already exists
        if (ratingRepository.existsByOrderId(ratingDTO.getOrderId())) {
            throw new RuntimeException("Rating already exists for order: " + ratingDTO.getOrderId());
        }

        // Validate rating values (1-5)
        validateRating(ratingDTO.getRestaurantRating(), "Restaurant rating");
        if (ratingDTO.getDeliveryRating() != null) {
            validateRating(ratingDTO.getDeliveryRating(), "Delivery rating");
        }

        // Create rating entity
        Rating rating = new Rating();
        rating.setOrderId(ratingDTO.getOrderId());
        rating.setUserId(ratingDTO.getUserId());
        rating.setRestaurantId(ratingDTO.getRestaurantId());
        rating.setDeliveryPartnerId(ratingDTO.getDeliveryPartnerId());
        rating.setRestaurantRating(ratingDTO.getRestaurantRating());
        rating.setDeliveryRating(ratingDTO.getDeliveryRating());
        rating.setFoodQuality(ratingDTO.getFoodQuality());
        rating.setDeliverySpeed(ratingDTO.getDeliverySpeed());
        rating.setComment(ratingDTO.getComment());

        Rating savedRating = ratingRepository.save(rating);
        log.info("✅ Rating created successfully with ID: {}", savedRating.getId());

        // Update restaurant average rating
        updateRestaurantRating(ratingDTO.getRestaurantId());

        return convertToDTO(savedRating);
    }

    /**
     * GET RATING BY ORDER ID
     */
    public RatingDTO getRatingByOrderId(Long orderId) {
        Rating rating = ratingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Rating not found for order: " + orderId));
        return convertToDTO(rating);
    }

    /**
     * GET ALL RATINGS BY USER
     */
    public List<RatingDTO> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET ALL RATINGS FOR RESTAURANT
     */
    public List<RatingDTO> getRatingsByRestaurantId(Long restaurantId) {
        return ratingRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET RESTAURANT RATING STATISTICS
     */
    public RatingStatsDTO getRestaurantStats(Long restaurantId) {
        Double avgRating = ratingRepository.calculateAverageRestaurantRating(restaurantId);
        long totalRatings = ratingRepository.countByRestaurantId(restaurantId);

        RatingStatsDTO stats = new RatingStatsDTO();
        stats.setEntityId(restaurantId);
        stats.setEntityType("RESTAURANT");
        stats.setAverageRating(avgRating != null ? avgRating : 0.0);
        stats.setTotalRatings(totalRatings);

        return stats;
    }

    /**
     * GET DELIVERY PARTNER RATING STATISTICS
     */
    public RatingStatsDTO getDeliveryPartnerStats(Long partnerId) {
        Double avgRating = ratingRepository.calculateAverageDeliveryRating(partnerId);
        List<Rating> ratings = ratingRepository.findByDeliveryPartnerId(partnerId);

        RatingStatsDTO stats = new RatingStatsDTO();
        stats.setEntityId(partnerId);
        stats.setEntityType("DELIVERY_PARTNER");
        stats.setAverageRating(avgRating != null ? avgRating : 0.0);
        stats.setTotalRatings((long) ratings.size());

        return stats;
    }

    /**
     * UPDATE RESTAURANT RATING (Call Restaurant Service)
     *
     * This would call Restaurant Service to update the average rating
     */
    private void updateRestaurantRating(Long restaurantId) {
        try {
            Double avgRating = ratingRepository.calculateAverageRestaurantRating(restaurantId);
            log.info("📊 Updated restaurant {} average rating: {}", restaurantId, avgRating);

            // In production, call Restaurant Service API to update rating
            // restTemplate.put("http://RESTAURANT-SERVICE/api/restaurants/" + restaurantId + "/rating", avgRating);

        } catch (Exception e) {
            log.error("Error updating restaurant rating: {}", e.getMessage());
        }
    }

    /**
     * VALIDATE RATING VALUE
     */
    private void validateRating(Integer rating, String fieldName) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 5");
        }
    }

    /**
     * CONVERT ENTITY TO DTO
     */
    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setOrderId(rating.getOrderId());
        dto.setUserId(rating.getUserId());
        dto.setRestaurantId(rating.getRestaurantId());
        dto.setDeliveryPartnerId(rating.getDeliveryPartnerId());
        dto.setRestaurantRating(rating.getRestaurantRating());
        dto.setDeliveryRating(rating.getDeliveryRating());
        dto.setFoodQuality(rating.getFoodQuality());
        dto.setDeliverySpeed(rating.getDeliverySpeed());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}

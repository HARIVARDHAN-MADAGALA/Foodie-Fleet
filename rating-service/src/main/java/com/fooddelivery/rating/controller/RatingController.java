package com.fooddelivery.rating.controller;

import com.fooddelivery.rating.dto.RatingDTO;
import com.fooddelivery.rating.dto.RatingStatsDTO;
import com.fooddelivery.rating.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RATING CONTROLLER - REST API
 *
 * Endpoints:
 * - POST /api/ratings - Create new rating
 * - GET /api/ratings/order/{orderId} - Get rating by order ID
 * - GET /api/ratings/user/{userId} - Get all ratings by user
 * - GET /api/ratings/restaurant/{restaurantId} - Get all ratings for restaurant
 * - GET /api/ratings/restaurant/{restaurantId}/stats - Get restaurant rating stats
 * - GET /api/ratings/partner/{partnerId}/stats - Get delivery partner rating stats
 */
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    /**
     * CREATE RATING
     */
    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@RequestBody RatingDTO ratingDTO) {
        log.info("📥 POST /api/ratings - Creating rating for Order ID: {}", ratingDTO.getOrderId());
        RatingDTO createdRating = ratingService.createRating(ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    /**
     * GET RATING BY ORDER ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<RatingDTO> getRatingByOrderId(@PathVariable Long orderId) {
        log.info("📥 GET /api/ratings/order/{}", orderId);
        RatingDTO rating = ratingService.getRatingByOrderId(orderId);
        return ResponseEntity.ok(rating);
    }

    /**
     * GET ALL RATINGS BY USER
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByUserId(@PathVariable Long userId) {
        log.info("📥 GET /api/ratings/user/{}", userId);
        List<RatingDTO> ratings = ratingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * GET ALL RATINGS FOR RESTAURANT
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByRestaurantId(@PathVariable Long restaurantId) {
        log.info("📥 GET /api/ratings/restaurant/{}", restaurantId);
        List<RatingDTO> ratings = ratingService.getRatingsByRestaurantId(restaurantId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * GET RESTAURANT RATING STATISTICS
     */
    @GetMapping("/restaurant/{restaurantId}/stats")
    public ResponseEntity<RatingStatsDTO> getRestaurantStats(@PathVariable Long restaurantId) {
        log.info("📥 GET /api/ratings/restaurant/{}/stats", restaurantId);
        RatingStatsDTO stats = ratingService.getRestaurantStats(restaurantId);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET DELIVERY PARTNER RATING STATISTICS
     */
    @GetMapping("/partner/{partnerId}/stats")
    public ResponseEntity<RatingStatsDTO> getDeliveryPartnerStats(@PathVariable Long partnerId) {
        log.info("📥 GET /api/ratings/partner/{}/stats", partnerId);
        RatingStatsDTO stats = ratingService.getDeliveryPartnerStats(partnerId);
        return ResponseEntity.ok(stats);
    }
}

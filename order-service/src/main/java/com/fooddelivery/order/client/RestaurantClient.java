package com.fooddelivery.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * RESTAURANT CLIENT SERVICE WITH CIRCUIT BREAKER
 *
 * This service calls the Restaurant Service to validate restaurant availability.
 * Circuit Breaker protects against cascading failures when Restaurant Service is down.
 *
 * HOW CIRCUIT BREAKER WORKS:
 *
 * 1. CLOSED STATE (Normal operation):
 *    - All requests pass through to Restaurant Service
 *    - Monitors failure rate
 *
 * 2. OPEN STATE (Service is failing):
 *    - After 50% failure rate (configured threshold)
 *    - Circuit "opens" and blocks all requests
 *    - Immediately returns fallback response
 *    - Waits 10 seconds before trying again
 *
 * 3. HALF-OPEN STATE (Testing recovery):
 *    - After wait duration, allows 3 test requests
 *    - If successful → Circuit closes (back to normal)
 *    - If failed → Circuit opens again
 *
 * BENEFITS:
 * - Prevents cascading failures across microservices
 * - Gives failing service time to recover
 * - Fast-fail with fallback responses
 * - Automatic recovery detection
 */
@Service
@Slf4j
public class RestaurantClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String RESTAURANT_SERVICE_URL = "http://restaurant-service";

    /**
     * Call Restaurant Service to check if restaurant is available
     *
     * @CircuitBreaker annotation configuration:
     * - name = "restaurantService" (matches config in application.yml)
     * - fallbackMethod = method to call when circuit is open
     *
     * Circuit breaker will:
     * - Track last 10 calls (slidingWindowSize = 10)
     * - Open if 50%+ fail (failureRateThreshold = 50)
     * - Wait 10 seconds before retry (waitDurationInOpenState = 10s)
     */
    @CircuitBreaker(name = "restaurantService", fallbackMethod = "getRestaurantFallback")
    public RestaurantDTO getRestaurant(Long restaurantId) {
        log.info("🔵 Circuit Breaker: Calling Restaurant Service for restaurant ID: {}", restaurantId);

        try {
            String url = RESTAURANT_SERVICE_URL + "/api/restaurants/" + restaurantId;
            RestaurantDTO restaurant = restTemplate.getForObject(url, RestaurantDTO.class);

            log.info("✅ Circuit Breaker: Successfully retrieved restaurant: {}",
                    restaurant != null ? restaurant.getName() : "null");
            return restaurant;

        } catch (Exception e) {
            log.error("❌ Circuit Breaker: Error calling Restaurant Service: {}", e.getMessage());
            throw e;  // Let circuit breaker handle it
        }
    }

    /**
     * FALLBACK METHOD
     *
     * Called when:
     * 1. Circuit is OPEN (too many failures)
     * 2. Service throws exception
     * 3. Timeout occurs
     *
     * Provides graceful degradation - returns cached/default data
     * instead of failing the entire order creation
     */
    public RestaurantDTO getRestaurantFallback(Long restaurantId, Throwable throwable) {
        log.warn("⚡ Circuit Breaker OPEN: Using fallback for restaurant ID: {}. Reason: {}",
                restaurantId, throwable.getMessage());

        // Return a default/cached restaurant object
        RestaurantDTO fallbackRestaurant = new RestaurantDTO();
        fallbackRestaurant.setId(restaurantId);
        fallbackRestaurant.setName("Restaurant (Service Unavailable)");
        fallbackRestaurant.setAvailable(true);  // Assume available to allow order

        log.info("🔄 Circuit Breaker: Returning fallback restaurant data");
        return fallbackRestaurant;
    }

    /**
     * Simple DTO for Restaurant data
     */
    public static class RestaurantDTO {
        private Long id;
        private String name;
        private Boolean available;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Boolean getAvailable() { return available; }
        public void setAvailable(Boolean available) { this.available = available; }
    }
}

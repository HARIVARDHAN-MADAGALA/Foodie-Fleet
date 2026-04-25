package com.fooddelivery.order.client;

import com.fooddelivery.order.exception.RestaurantServiceException;
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
//        api/restaurants/
        try {
            String url = RESTAURANT_SERVICE_URL + "/restaurants/" + restaurantId;
            RestaurantDTO restaurant = restTemplate.getForObject(url, RestaurantDTO.class);

            log.info("✅ Circuit Breaker: Successfully retrieved restaurant: {}",
                    restaurant != null ? restaurant.getName() : "null");
            return restaurant;

        } catch (Exception e) {
            log.error("❌ Circuit Breaker: Error calling Restaurant Service: {}", e.getMessage());
            throw new RestaurantServiceException("Failed to communicate with Restaurant Service", e);
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
     * Instead of returning default data, throws exception with clear message
     * This allows proper error handling at the service layer
     */
    public RestaurantDTO getRestaurantFallback(Long restaurantId, Throwable throwable) {
        log.error("⚡ Circuit Breaker OPEN: Fallback triggered for restaurant ID: {}. Reason: {}",
                restaurantId, throwable.getMessage());

        // Throw custom exception to be handled by GlobalExceptionHandler
        throw new RestaurantServiceException(
                "Restaurant Service is temporarily unavailable. Circuit breaker is OPEN. Please try again later.",
                throwable
        );
    }

    /**
     * Simple DTO for Restaurant data
     */
    public static class RestaurantDTO {
        private Long id;
        private String name;
        private Boolean isActive;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }
}

package com.fooddelivery.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * REST TEMPLATE CONFIGURATION
 *
 * Configures RestTemplate for making HTTP calls to other microservices.
 *
 * @LoadBalanced annotation enables:
 * - Service discovery via Eureka
 * - Client-side load balancing
 * - Automatic service URL resolution (e.g., http://restaurant-service)
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a load-balanced RestTemplate bean
     *
     * This allows calling services by their Eureka registered name:
     * - "http://restaurant-service/api/restaurants/1"
     * - Eureka resolves to actual IP:PORT
     * - Load balances if multiple instances exist
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

package com.fooddelivery.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * RESTAURANT SERVICE - Restaurant and Menu Management
 * 
 * Purpose: Manages all restaurant-related operations including:
 * - Restaurant registration and profile management
 * - Menu items (add, update, delete)
 * - Restaurant search and filtering by cuisine
 * - Caching restaurant data for better performance
 * 
 * Technologies Used:
 * 1. MySQL: Stores restaurant and menu data persistently
 * 2. Redis: Caches frequently accessed restaurant data
 * 3. Spring Data JPA: ORM for database operations
 * 4. Eureka: Registers this service for discovery
 * 
 * @EnableCaching: Enables Spring's annotation-driven cache management
 * @EnableDiscoveryClient: Registers this service with Eureka Server
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🍽️  Restaurant Service Started!");
        System.out.println("📍 Port: 8081");
        System.out.println("==========================================");
    }
}

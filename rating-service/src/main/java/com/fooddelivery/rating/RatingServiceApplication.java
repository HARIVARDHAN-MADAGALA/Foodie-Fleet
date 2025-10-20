package com.fooddelivery.rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * RATING SERVICE - Main Application
 *
 * This service handles ratings and reviews for orders, restaurants, and delivery partners.
 *
 * Responsibilities:
 * - Collect ratings from users after order delivery
 * - Calculate average ratings for restaurants
 * - Calculate average ratings for delivery partners
 * - Provide rating history and analytics
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RatingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingServiceApplication.class, args);
        System.out.println("⭐ Rating Service Started Successfully on Port 8087!");
    }
}

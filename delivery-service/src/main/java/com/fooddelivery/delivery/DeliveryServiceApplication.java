package com.fooddelivery.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * DELIVERY SERVICE - Main Application
 *
 * Handles delivery partner assignment and tracking
 *
 * ✅ Kafka ENABLED - Listens to order-events and delivery-events!
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚚 Delivery Service Started!");
        System.out.println("📍 Port: 8084");
        System.out.println("🎧 Kafka Consumer ENABLED!");
        System.out.println("==========================================");
    }
}

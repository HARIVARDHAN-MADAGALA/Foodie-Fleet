package com.fooddelivery.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * PAYMENT SERVICE - Main Application
 *
 * This microservice handles payment processing for food delivery orders.
 *
 * Responsibilities:
 * - Process payments for orders
 * - Listen to Kafka events from Order Service
 * - Validate payment methods
 * - Handle refunds
 * - Publish payment status events
 *
 * Technologies Used:
 * - Spring Boot - Application framework
 * - Spring Cloud Netflix Eureka - Service discovery
 * - Spring Kafka - Event-driven communication
 * - Spring Data JPA - Database access
 * - MySQL - Data storage
 *
 * ✅ Kafka ENABLED - Listens to order-events and delivery-events!
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("💳 Payment Service Started!");
        System.out.println("📍 Port: 8085");
        System.out.println("🎧 Kafka Consumer ENABLED!");
        System.out.println("==========================================");
    }
}

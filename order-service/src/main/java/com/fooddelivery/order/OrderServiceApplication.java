package com.fooddelivery.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * ORDER SERVICE - Order Management with Kafka
 * 
 * Purpose: Central service for order management
 * - Order placement and tracking
 * - Order status updates
 * - Order history
 * - Event publishing to Kafka
 * - Real-time order tracking via WebSocket
 * 
 * Key Technologies:
 * 1. Kafka: Publishes order events to other services
 * 2. WebSocket: Real-time order status updates to clients
 * 3. MySQL: Persistent order storage
 * 
 * Event Flow:
 * Order Created → Kafka Event → Payment Service, Notification Service
 * Payment Success → Order Updated → Delivery Assignment
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🛒 Order Service Started!");
        System.out.println("📍 Port: 8083");
        System.out.println("📡 Kafka Enabled");
        System.out.println("🔌 WebSocket Enabled");
        System.out.println("==========================================");
    }
}

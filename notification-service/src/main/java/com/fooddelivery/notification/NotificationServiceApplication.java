package com.fooddelivery.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * NOTIFICATION SERVICE - Main Application
 *
 * This service listens to ALL Kafka events and sends notifications
 * to users via email, SMS, and push notifications.
 *
 * Responsibilities:
 * - Listen to order, payment, delivery, and rating events
 * - Send notifications to users
 * - Log all events for audit trail
 * - Provide notification history
 *
 * NOTE: Database auto-configuration disabled - this service doesn't need a database.
 * Kafka ENABLED - listens to all event topics!
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
        System.out.println("📧 Notification Service Started Successfully on Port 8086!");
        System.out.println("🎧 Kafka Consumer ENABLED - Listening to events!");
    }
}

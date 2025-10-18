package com.fooddelivery.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * USER SERVICE - User Management
 * 
 * Purpose: Handles all user-related operations:
 * - User registration and login
 * - User profile management
 * - Delivery address management
 * - Session management with Redis
 * 
 * Key Features:
 * 1. Simple authentication (can be enhanced with Spring Security & JWT)
 * 2. User profile CRUD operations
 * 3. Multiple delivery addresses per user
 * 4. Redis caching for user sessions
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("👤 User Service Started!");
        System.out.println("📍 Port: 8082");
        System.out.println("==========================================");
    }
}

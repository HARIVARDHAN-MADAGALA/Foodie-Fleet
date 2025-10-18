package com.fooddelivery.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API GATEWAY - Single Entry Point
 * 
 * Purpose: Acts as a single entry point for all client requests.
 * Routes requests to appropriate microservices based on URL patterns.
 * 
 * Key Concepts:
 * 1. Request Routing: Directs requests to the correct microservice
 * 2. Load Balancing: Distributes requests across multiple service instances
 * 3. Cross-cutting Concerns: Handles logging, security, rate limiting
 * 4. Service Discovery Integration: Automatically finds services via Eureka
 * 
 * Benefits:
 * - Clients only need to know one endpoint (localhost:8080)
 * - Centralized security and monitoring
 * - Simplifies client-side code
 * 
 * @EnableDiscoveryClient: Enables this service to discover other services via Eureka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 API Gateway Started Successfully!");
        System.out.println("🌐 Gateway URL: http://localhost:8080");
        System.out.println("==========================================");
    }
}

package com.fooddelivery.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EUREKA SERVER - Service Discovery
 * 
 * Purpose: Acts as a registry where all microservices register themselves.
 * Other services can discover and communicate with each other through Eureka.
 * 
 * Key Concepts:
 * 1. Service Registration: All microservices register with Eureka on startup
 * 2. Service Discovery: Services can find other services dynamically
 * 3. Health Monitoring: Eureka tracks the health of all registered services
 * 4. Load Balancing: Helps distribute requests across multiple instances
 * 
 * @EnableEurekaServer: Enables this application as a Eureka Server
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 Eureka Server Started Successfully!");
        System.out.println("📊 Dashboard: http://localhost:8761");
        System.out.println("==========================================");
    }
}

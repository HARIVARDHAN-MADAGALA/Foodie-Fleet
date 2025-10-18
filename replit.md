# Food Delivery Microservices - Learning Project

## Project Overview
Complete Java microservices learning project implementing an online food delivery system (like Swiggy/Zomato). Built to teach microservices architecture, Spring Boot, Spring Cloud, Kafka, Redis, and MySQL.

## Architecture
- **7 Microservices**: Restaurant, User, Order, Delivery, Payment, Notification, Rating
- **Infrastructure**: Eureka Server (Service Discovery), API Gateway
- **Technologies**: Spring Boot 3.2, Spring Cloud, Kafka, Redis, MySQL, WebSocket
- **Patterns**: Event-driven architecture, Database per service, API Gateway, Service Discovery

## Services
1. **Eureka Server** (8761) - Service registry
2. **API Gateway** (8080) - Single entry point
3. **Restaurant Service** (8081) - Restaurant & menu management with Redis caching
4. **User Service** (8082) - User authentication and profiles with Redis sessions
5. **Order Service** (8083) - Order management with Kafka events & WebSocket tracking
6. **Delivery Service** (8084) - Delivery partner assignment
7. **Payment Service** (8085) - Payment processing simulation
8. **Notification Service** (8086) - Kafka consumer for notifications
9. **Rating Service** (8087) - Reviews and ratings

## Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (6 databases)
- Redis Server
- Apache Kafka + Zookeeper

## Quick Start
1. Create MySQL databases (see SETUP_GUIDE.md)
2. Start Redis: `redis-server`
3. Start Kafka & Zookeeper
4. Build: `mvn clean install`
5. Start services in order (see SETUP_GUIDE.md)

## Key Learning Concepts
- Microservices communication (sync REST + async Kafka)
- Service discovery with Eureka
- API Gateway routing
- Event-driven architecture
- Caching with Redis
- Real-time updates with WebSocket
- Database per service pattern

## Documentation
- `README.md` - Complete project documentation
- `SETUP_GUIDE.md` - Step-by-step setup for IntelliJ IDEA
- `COMPLETE_SERVICES_CODE.md` - All service code with copy-paste guide

## Current Status
All 9 services implemented with complete code. Ready for local development and testing.

## Next Enhancements
- Spring Security + JWT
- Circuit Breaker (Resilience4j)
- Distributed Tracing (Sleuth + Zipkin)
- Docker containerization
- Kubernetes deployment

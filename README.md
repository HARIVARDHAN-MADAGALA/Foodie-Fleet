# Food Delivery Microservices System

A comprehensive learning project implementing an online food delivery system (like Swiggy/Zomato) using Java microservices architecture.

## 🏗️ Architecture Overview

This project demonstrates a complete microservices implementation with the following components:

### Microservices (7 Services)
1. **Restaurant Service** (Port 8081) - Manage restaurants, menus, cuisines
2. **User Service** (Port 8082) - Customer authentication, profiles, addresses
3. **Order Service** (Port 8083) - Order placement, tracking, history
4. **Delivery Service** (Port 8084) - Delivery partner management, tracking
5. **Payment Service** (Port 8085) - Payment processing, refunds
6. **Notification Service** (Port 8086) - SMS/Email/Push notifications
7. **Rating Service** (Port 8087) - Reviews and ratings

### Infrastructure Services
- **Eureka Server** (Port 8761) - Service Discovery & Registration
- **API Gateway** (Port 8080) - Single entry point, routing, load balancing

## 🛠️ Technologies Used

- **Java 17** - Programming language
- **Spring Boot 3.2** - Microservices framework
- **Spring Cloud** - Microservices infrastructure
  - Netflix Eureka - Service Discovery
  - Spring Cloud Gateway - API Gateway
- **Apache Kafka** - Event streaming & async communication
- **Redis** - Caching layer
- **MySQL** - Relational database
- **WebSocket** - Real-time order tracking
- **Maven** - Build tool & dependency management

## 📋 Prerequisites

Before running this project, ensure you have:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis Server
- Apache Kafka
- IntelliJ IDEA (recommended) or any Java IDE

## 🚀 Getting Started

### Step 1: Database Setup

Create databases for each service in MySQL:

```sql
CREATE DATABASE restaurant_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
CREATE DATABASE payment_db;
CREATE DATABASE rating_db;
```

### Step 2: Start Infrastructure Services

1. **Start Redis Server**
   ```bash
   redis-server
   ```

2. **Start Kafka & Zookeeper**
   ```bash
   # Start Zookeeper
   bin/zookeeper-server-start.sh config/zookeeper.properties
   
   # Start Kafka
   bin/kafka-server-start.sh config/server.properties
   ```

### Step 3: Build All Services

```bash
mvn clean install
```

### Step 4: Start Services in Order

Start services in this specific order:

1. **Eureka Server** (wait until fully started)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

2. **API Gateway** (wait for Eureka registration)
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

3. **All Microservices** (can be started in parallel)
   ```bash
   cd restaurant-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd delivery-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   cd rating-service && mvn spring-boot:run
   ```

### Step 5: Verify Services

1. Open Eureka Dashboard: http://localhost:8761
2. Verify all services are registered
3. Test API Gateway: http://localhost:8080

## 📚 Project Structure

```
food-delivery-system/
├── eureka-server/              # Service Discovery
├── api-gateway/                # API Gateway
├── restaurant-service/         # Restaurant Management
├── user-service/               # User Management
├── order-service/              # Order Management
├── delivery-service/           # Delivery Management
├── payment-service/            # Payment Processing
├── notification-service/       # Notifications
├── rating-service/             # Ratings & Reviews
└── pom.xml                     # Parent POM
```

## 🔌 API Endpoints

All APIs are accessible through the API Gateway at `http://localhost:8080`

### Restaurant Service
- `POST /api/restaurants` - Create restaurant
- `GET /api/restaurants` - Get all restaurants
- `GET /api/restaurants/{id}` - Get restaurant by ID
- `POST /api/restaurants/{id}/menu` - Add menu item
- `GET /api/restaurants/{id}/menu` - Get restaurant menu

### User Service
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/{id}` - Get user profile
- `POST /api/users/{id}/addresses` - Add delivery address

### Order Service
- `POST /api/orders` - Place new order
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders/user/{userId}` - Get user order history
- `PUT /api/orders/{id}/status` - Update order status
- `WebSocket: ws://localhost:8083/order-tracking` - Real-time tracking

### Delivery Service
- `POST /api/delivery/partners` - Register delivery partner
- `GET /api/delivery/partners/available` - Get available partners
- `POST /api/delivery/assign/{orderId}` - Assign delivery partner
- `PUT /api/delivery/{id}/location` - Update delivery location

### Payment Service
- `POST /api/payments/process` - Process payment
- `GET /api/payments/{orderId}` - Get payment status
- `POST /api/payments/{id}/refund` - Process refund

### Rating Service
- `POST /api/ratings/restaurant` - Rate restaurant
- `POST /api/ratings/delivery` - Rate delivery partner
- `GET /api/ratings/restaurant/{restaurantId}` - Get restaurant ratings
- `GET /api/ratings/delivery/{partnerId}` - Get delivery partner ratings

## 🎯 Key Learning Concepts

### 1. Service Discovery (Eureka)
- Services automatically register with Eureka Server
- Client-side load balancing
- Service health checks

### 2. API Gateway Pattern
- Single entry point for all client requests
- Request routing to appropriate microservices
- Cross-cutting concerns (logging, security)

### 3. Event-Driven Architecture (Kafka)
- Async communication between services
- Event publishing and consuming
- Topics: `order-events`, `payment-events`, `notification-events`

### 4. Caching (Redis)
- Restaurant data caching
- User session management
- Improved performance

### 5. Database per Service Pattern
- Each service has its own database
- Data isolation and independence
- Service autonomy

### 6. Real-time Communication (WebSocket)
- Live order status updates
- Real-time delivery tracking
- Bi-directional communication

## 🧪 Testing the System

### Complete Order Flow

1. **Register a Restaurant**
   ```bash
   curl -X POST http://localhost:8080/api/restaurants \
   -H "Content-Type: application/json" \
   -d '{"name":"Pizza Paradise","cuisine":"Italian","address":"123 Main St"}'
   ```

2. **Register a User**
   ```bash
   curl -X POST http://localhost:8080/api/users/register \
   -H "Content-Type: application/json" \
   -d '{"name":"John Doe","email":"john@example.com","phone":"1234567890"}'
   ```

3. **Place an Order**
   ```bash
   curl -X POST http://localhost:8080/api/orders \
   -H "Content-Type: application/json" \
   -d '{"userId":1,"restaurantId":1,"items":[{"menuItemId":1,"quantity":2}]}'
   ```

4. **Process Payment**
   ```bash
   curl -X POST http://localhost:8080/api/payments/process \
   -H "Content-Type: application/json" \
   -d '{"orderId":1,"amount":500,"paymentMethod":"CARD"}'
   ```

5. **Assign Delivery Partner**
   ```bash
   curl -X POST http://localhost:8080/api/delivery/assign/1
   ```

6. **Track Order (WebSocket)**
   - Connect to: `ws://localhost:8083/order-tracking`
   - Subscribe to order updates

## 📖 Detailed Documentation

Each service contains detailed inline comments explaining:
- Configuration files
- Entity models
- Repository patterns
- Service layer logic
- REST controllers
- Event producers/consumers
- Exception handling

## 🔄 Inter-Service Communication

### Synchronous (REST)
- API Gateway → All Services
- Order Service → Restaurant Service (menu validation)
- Order Service → User Service (user validation)

### Asynchronous (Kafka Events)
- Order Created → Payment Service, Notification Service
- Payment Success → Delivery Service, Notification Service
- Delivery Assigned → Notification Service, Order Service
- Order Delivered → Rating Service, Notification Service

## 🎓 Learning Objectives

By building and running this project, you will learn:

1. ✅ Microservices architecture principles
2. ✅ Service discovery and registration
3. ✅ API Gateway pattern
4. ✅ Event-driven architecture
5. ✅ Distributed caching
6. ✅ Database per service pattern
7. ✅ Real-time communication with WebSocket
8. ✅ RESTful API design
9. ✅ Spring Boot best practices
10. ✅ Maven multi-module projects

## 🐛 Troubleshooting

### Services not registering with Eureka
- Ensure Eureka Server is running first
- Check network connectivity
- Verify `eureka.client.service-url.defaultZone` in application.yml

### Kafka connection issues
- Ensure Kafka and Zookeeper are running
- Check Kafka broker address in application.yml
- Verify topics are created

### Database connection errors
- Verify MySQL is running
- Check database credentials in application.yml
- Ensure databases are created

### Redis connection issues
- Start Redis server
- Check Redis host/port in application.yml

## 📝 Notes for IntelliJ IDEA

1. Import as Maven project
2. Enable annotation processing for Lombok
3. Set Java SDK to 17
4. Build project (Ctrl+F9)
5. Run services using Spring Boot run configuration

## 🚀 Next Steps

After mastering this project, consider adding:
- Spring Security with JWT authentication
- Circuit Breaker pattern (Resilience4j)
- Distributed tracing (Sleuth + Zipkin)
- Centralized logging (ELK Stack)
- Docker containerization
- Kubernetes deployment

## 📄 License

This project is for educational purposes.

---

**Happy Learning! 🎉**
=======
# Foodie-Fleet
Microservices-based food delivery system (like Swiggy/Zomato) built with Spring Boot, Spring Cloud, Kafka, Redis, and MySQL. Implements event-driven architecture, Eureka service discovery, API Gateway, and real-time notifications for efficient and scalable delivery management.

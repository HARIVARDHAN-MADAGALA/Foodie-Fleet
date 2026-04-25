# 🚀 Complete Setup Guide - Food Delivery Microservices

## Step-by-Step Setup Instructions for IntelliJ IDEA

### Prerequisites Installation

#### 1. Install Java 17
```bash
# Check Java version
java -version

# If not installed, download from:
# https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
```

#### 2. Install MySQL 8.0+
```bash
# Start MySQL service
# Create required databases
mysql -u root -p

CREATE DATABASE restaurant_db;
CREATE DATABASE user_db;
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
CREATE DATABASE payment_db;
CREATE DATABASE rating_db;
```

#### 3. Install Redis
```bash
# Download from: https://redis.io/download
# Start Redis server
redis-server
```

#### 4. Install Apache Kafka
```bash
# Download from: https://kafka.apache.org/downloads

# Start Zookeeper (Terminal 1)
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka (Terminal 2)
bin/kafka-server-start.sh config/server.properties
```

### Import Project into IntelliJ IDEA

#### Option 1: Import as Single Multi-Module Project
1. Open IntelliJ IDEA
2. File → Open → Select the root `food-delivery-system` folder
3. Wait for Maven to import all modules
4. IntelliJ will detect all 9 modules automatically

#### Option 2: Import Individual Services
1. File → Open → Select each service folder individually
2. Repeat for all 9 services

### Configure IntelliJ

1. **Set Java SDK to 17**
   - File → Project Structure → Project → SDK → Select Java 17

2. **Enable Annotation Processing** (for Lombok)
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

3. **Build the Project**
   - Build → Build Project (Ctrl+F9)

### Starting Services

**IMPORTANT: Start services in this exact order!**

#### 1. Start Infrastructure Services

**Terminal 1: Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```
Wait until you see: "Eureka Server Started Successfully!"
Access dashboard: http://localhost:8761

**Terminal 2: API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```
Wait until registered with Eureka

#### 2. Start Microservices (can run in parallel)

**Terminal 3: Restaurant Service**
```bash
cd restaurant-service
mvn spring-boot:run
```

**Terminal 4: User Service**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 5: Order Service**
```bash
cd order-service
mvn spring-boot:run
```

**Terminal 6: Delivery Service**
```bash
cd delivery-service
mvn spring-boot:run
```

**Terminal 7: Payment Service**
```bash
cd payment-service
mvn spring-boot:run
```

**Terminal 8: Notification Service**
```bash
cd notification-service
mvn spring-boot:run
```

**Terminal 9: Rating Service**
```bash
cd rating-service
mvn spring-boot:run
```

### Verify All Services

1. **Check Eureka Dashboard**: http://localhost:8761
   - All 7 microservices should be registered

2. **Check Service Health**:
   ```bash
   curl http://localhost:8081/restaurants  # Restaurant Service
   curl http://localhost:8082              # User Service
   curl http://localhost:8083              # Order Service
   curl http://localhost:8084/partners     # Delivery Service
   curl http://localhost:8085/payments     # Payment Service
   curl http://localhost:8087/ratings      # Rating Service
   ```

### Service Ports Reference

| Service | Port | URL |
|---------|------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Restaurant Service | 8081 | http://localhost:8081 |
| User Service | 8082 | http://localhost:8082 |
| Order Service | 8083 | http://localhost:8083 |
| Delivery Service | 8084 | http://localhost:8084 |
| Payment Service | 8085 | http://localhost:8085 |
| Notification Service | 8086 | http://localhost:8086 |
| Rating Service | 8087 | http://localhost:8087 |

### Testing Complete Order Flow

#### 1. Create a Restaurant
```bash
curl -X POST http://localhost:8080/api/restaurants \
-H "Content-Type: application/json" \
-d '{
  "name": "Pizza Paradise",
  "cuisine": "Italian",
  "address": "123 Main Street",
  "phone": "1234567890",
  "email": "info@pizzaparadise.com"
}'
```

#### 2. Add Menu Items
```bash
curl -X POST http://localhost:8080/api/restaurants/1/menu \
-H "Content-Type: application/json" \
-d '{
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato and mozzarella",
  "price": 299.99,
  "category": "Main Course",
  "isVegetarian": true,
  "restaurantId": 1
}'
```

#### 3. Register a User
```bash
curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "password": "password123"
}'
```

#### 4. Add User Address
```bash
curl -X POST http://localhost:8080/api/users/1/addresses \
-H "Content-Type: application/json" \
-d '{
  "addressLine1": "456 Oak Avenue",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "addressType": "HOME",
  "isDefault": true,
  "userId": 1
}'
```

#### 5. Register Delivery Partner
```bash
curl -X POST http://localhost:8080/api/delivery/partners \
-H "Content-Type: application/json" \
-d '{
  "name": "Delivery Partner 1",
  "phone": "5555555555",
  "vehicleType": "BIKE",
  "vehicleNumber": "MH01AB1234"
}'
```

#### 6. Place an Order
```bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{
  "userId": 1,
  "restaurantId": 1,
  "addressId": 1,
  "items": [
    {
      "menuItemId": 1,
      "itemName": "Margherita Pizza",
      "quantity": 2,
      "price": 299.99
    }
  ],
  "deliveryFee": 50.0,
  "discount": 0.0
}'
```

#### 7. Process Payment
```bash
curl -X POST http://localhost:8080/api/payments/process \
-H "Content-Type: application/json" \
-d '{
  "orderId": 1,
  "amount": 649.98,
  "paymentMethod": "CARD"
}'
```

#### 8. Assign Delivery Partner
```bash
curl -X POST http://localhost:8080/api/delivery/assign/1
```

#### 9. Update Order Status
```bash
curl -X PUT "http://localhost:8080/api/orders/1/status?status=PICKED_UP"
curl -X PUT "http://localhost:8080/api/orders/1/status?status=DELIVERED"
```

#### 10. Rate Restaurant
```bash
curl -X POST http://localhost:8080/api/ratings/restaurant \
-H "Content-Type: application/json" \
-d '{
  "userId": 1,
  "restaurantId": 1,
  "rating": 5.0,
  "comment": "Excellent food and quick delivery!"
}'
```

### Troubleshooting

#### Services not registering with Eureka
- Ensure Eureka Server started first and is fully running
- Check `application.yml` eureka configuration
- Verify network connectivity

#### Kafka connection errors
- Ensure Zookeeper is running
- Ensure Kafka broker is running
- Check Kafka logs for errors

#### Database connection errors
- Verify MySQL is running
- Check database credentials in `application.yml`
- Ensure all databases are created

#### Redis connection errors
- Start Redis server: `redis-server`
- Check Redis port (default 6379)

#### Port already in use
- Kill process using the port
- Or change port in `application.yml`

### IntelliJ Run Configurations

Create separate Run Configurations for each service:
1. Run → Edit Configurations
2. Click + → Spring Boot
3. Name: Service name (e.g., "Eureka Server")
4. Module: Select corresponding module
5. Main class: Application class
6. Repeat for all 9 services

### Understanding the Flow

```
1. User places order → Order Service
2. Order Service publishes "ORDER_CREATED" event → Kafka
3. Payment Service consumes event → Processes payment
4. Payment Service updates order payment status
5. Order Service publishes "PAYMENT_COMPLETED" event → Kafka
6. Delivery Service consumes event → Assigns delivery partner
7. Notification Service consumes all events → Sends notifications
8. Order delivered → Rating Service allows user to rate
```

### Kafka Topics

- `order-events`: Order creation and updates
- `payment-events`: Payment status updates
- `notification-events`: Notification triggers

### Next Learning Steps

After running this project successfully:
1. Add Spring Security with JWT authentication
2. Implement Circuit Breaker with Resilience4j
3. Add distributed tracing with Sleuth + Zipkin
4. Centralized configuration with Spring Cloud Config
5. Docker containerization
6. Kubernetes deployment

### Getting Help

- Check logs in each service terminal
- Verify Eureka dashboard for service registration
- Use Postman collection (create from curl commands)
- Check MySQL databases for data persistence

---

**Happy Learning! 🎓**

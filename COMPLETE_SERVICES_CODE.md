# Complete Services Code - Copy & Paste Guide

This file contains ALL remaining service code. Copy each section to its respective file path.

## DELIVERY SERVICE

### File: delivery-service/src/main/resources/application.yml
```yaml
server:
  port: 8084

spring:
  application:
    name: delivery-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/delivery_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: delivery-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

logging:
  level:
    com.fooddelivery.delivery: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/DeliveryServiceApplication.java
```java
package com.fooddelivery.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
        System.out.println("🚚 Delivery Service Started on Port 8084!");
    }
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/entity/DeliveryPartner.java
```java
package com.fooddelivery.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_partners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    private String vehicleNumber;
    
    @Enumerated(EnumType.STRING)
    private PartnerStatus status = PartnerStatus.AVAILABLE;
    
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/entity/VehicleType.java
```java
package com.fooddelivery.delivery.entity;

public enum VehicleType {
    BIKE, SCOOTER, CAR
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/entity/PartnerStatus.java
```java
package com.fooddelivery.delivery.entity;

public enum PartnerStatus {
    AVAILABLE, BUSY, OFFLINE
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/repository/DeliveryPartnerRepository.java
```java
package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.entity.PartnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    List<DeliveryPartner> findByStatus(PartnerStatus status);
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/dto/DeliveryPartnerDTO.java
```java
package com.fooddelivery.delivery.dto;

import com.fooddelivery.delivery.entity.PartnerStatus;
import com.fooddelivery.delivery.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartnerDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    private VehicleType vehicleType;
    private String vehicleNumber;
    private PartnerStatus status;
    private Double rating;
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/service/DeliveryService.java
```java
package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.dto.DeliveryPartnerDTO;
import com.fooddelivery.delivery.entity.*;
import com.fooddelivery.delivery.repository.DeliveryPartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryPartnerRepository partnerRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public DeliveryPartnerDTO registerPartner(DeliveryPartnerDTO dto) {
        DeliveryPartner partner = new DeliveryPartner();
        partner.setName(dto.getName());
        partner.setPhone(dto.getPhone());
        partner.setVehicleType(dto.getVehicleType());
        partner.setVehicleNumber(dto.getVehicleNumber());
        partner.setStatus(PartnerStatus.AVAILABLE);
        
        DeliveryPartner saved = partnerRepository.save(partner);
        log.info("Delivery partner registered: {}", saved.getId());
        return convertToDTO(saved);
    }

    public List<DeliveryPartnerDTO> getAvailablePartners() {
        return partnerRepository.findByStatus(PartnerStatus.AVAILABLE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignDelivery(Long orderId) {
        List<DeliveryPartner> available = partnerRepository.findByStatus(PartnerStatus.AVAILABLE);
        if (available.isEmpty()) {
            throw new RuntimeException("No delivery partners available");
        }
        
        DeliveryPartner partner = available.get(0);
        partner.setStatus(PartnerStatus.BUSY);
        partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
        partnerRepository.save(partner);
        
        String orderServiceUrl = "http://localhost:8083/" + orderId + "/assign-delivery?deliveryPartnerId=" + partner.getId();
        try {
            restTemplate.put(orderServiceUrl, null);
            log.info("Assigned partner {} to order {}", partner.getId(), orderId);
        } catch (Exception e) {
            log.error("Error updating order: {}", e.getMessage());
        }
    }

    @Transactional
    public void updatePartnerLocation(Long partnerId, Double latitude, Double longitude) {
        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        partner.setCurrentLatitude(latitude);
        partner.setCurrentLongitude(longitude);
        partnerRepository.save(partner);
    }

    @Transactional
    public void markAvailable(Long partnerId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        partner.setStatus(PartnerStatus.AVAILABLE);
        partnerRepository.save(partner);
    }

    private DeliveryPartnerDTO convertToDTO(DeliveryPartner partner) {
        DeliveryPartnerDTO dto = new DeliveryPartnerDTO();
        dto.setId(partner.getId());
        dto.setName(partner.getName());
        dto.setPhone(partner.getPhone());
        dto.setVehicleType(partner.getVehicleType());
        dto.setVehicleNumber(partner.getVehicleNumber());
        dto.setStatus(partner.getStatus());
        dto.setRating(partner.getRating());
        return dto;
    }
}
```

### File: delivery-service/src/main/java/com/fooddelivery/delivery/controller/DeliveryController.java
```java
package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.dto.DeliveryPartnerDTO;
import com.fooddelivery.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/partners")
    public ResponseEntity<DeliveryPartnerDTO> registerPartner(@Valid @RequestBody DeliveryPartnerDTO dto) {
        return new ResponseEntity<>(deliveryService.registerPartner(dto), HttpStatus.CREATED);
    }

    @GetMapping("/partners/available")
    public ResponseEntity<List<DeliveryPartnerDTO>> getAvailablePartners() {
        return ResponseEntity.ok(deliveryService.getAvailablePartners());
    }

    @PostMapping("/assign/{orderId}")
    public ResponseEntity<Void> assignDelivery(@PathVariable Long orderId) {
        deliveryService.assignDelivery(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{partnerId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long partnerId,
                                               @RequestParam Double latitude,
                                               @RequestParam Double longitude) {
        deliveryService.updatePartnerLocation(partnerId, latitude, longitude);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{partnerId}/available")
    public ResponseEntity<Void> markAvailable(@PathVariable Long partnerId) {
        deliveryService.markAvailable(partnerId);
        return ResponseEntity.ok().build();
    }
}
```

## PAYMENT SERVICE

### File: payment-service/src/main/resources/application.yml
```yaml
server:
  port: 8085

spring:
  application:
    name: payment-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/payment_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: payment-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### File: payment-service/src/main/java/com/fooddelivery/payment/PaymentServiceApplication.java
```java
package com.fooddelivery.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("💳 Payment Service Started on Port 8085!");
    }
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/entity/Payment.java
```java
package com.fooddelivery.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long orderId;
    
    @Column(nullable = false)
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private String transactionId;
    private LocalDateTime paymentTime = LocalDateTime.now();
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/entity/PaymentMethod.java
```java
package com.fooddelivery.payment.entity;

public enum PaymentMethod {
    CARD, UPI, NET_BANKING, CASH_ON_DELIVERY
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/entity/PaymentStatus.java
```java
package com.fooddelivery.payment.entity;

public enum PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/repository/PaymentRepository.java
```java
package com.fooddelivery.payment.repository;

import com.fooddelivery.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/dto/PaymentRequestDTO.java
```java
package com.fooddelivery.payment.dto;

import com.fooddelivery.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    @NotNull
    private Long orderId;
    @NotNull
    private Double amount;
    @NotNull
    private PaymentMethod paymentMethod;
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/service/PaymentService.java
```java
package com.fooddelivery.payment.service;

import com.fooddelivery.payment.dto.PaymentRequestDTO;
import com.fooddelivery.payment.entity.*;
import com.fooddelivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public Payment processPayment(PaymentRequestDTO request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        
        boolean paymentSuccess = simulatePaymentGateway(request);
        
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN_" + UUID.randomUUID().toString());
            log.info("Payment successful for order: {}", request.getOrderId());
            
            updateOrderPaymentStatus(request.getOrderId(), "COMPLETED");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.error("Payment failed for order: {}", request.getOrderId());
            
            updateOrderPaymentStatus(request.getOrderId(), "FAILED");
        }
        
        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGateway(PaymentRequestDTO request) {
        try {
            Thread.sleep(1000);
            return request.getPaymentMethod() != PaymentMethod.CASH_ON_DELIVERY || Math.random() > 0.1;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private void updateOrderPaymentStatus(Long orderId, String status) {
        String url = "http://localhost:8083/" + orderId + "/payment-status?paymentStatus=" + status;
        try {
            restTemplate.put(url, null);
        } catch (Exception e) {
            log.error("Failed to update order payment status: {}", e.getMessage());
        }
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
}
```

### File: payment-service/src/main/java/com/fooddelivery/payment/controller/PaymentController.java
```java
package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.PaymentRequestDTO;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
```

## NOTIFICATION SERVICE

### File: notification-service/src/main/resources/application.yml
```yaml
server:
  port: 8086

spring:
  application:
    name: notification-service
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### File: notification-service/src/main/java/com/fooddelivery/notification/NotificationServiceApplication.java
```java
package com.fooddelivery.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
        System.out.println("🔔 Notification Service Started on Port 8086!");
    }
}
```

### File: notification-service/src/main/java/com/fooddelivery/notification/kafka/OrderEventConsumer.java
```java
package com.fooddelivery.notification.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(String event) {
        log.info("📧 Received order event: {}", event);
        log.info("📱 Sending notification to user...");
        log.info("✅ Notification sent successfully!");
    }
}
```

### File: notification-service/src/main/java/com/fooddelivery/notification/service/NotificationService.java
```java
package com.fooddelivery.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    public void sendOrderConfirmation(Long userId, Long orderId) {
        log.info("📧 Sending order confirmation email to user {}", userId);
        log.info("✉️ Order {} confirmed!", orderId);
    }
    
    public void sendPaymentNotification(Long userId, String status) {
        log.info("💳 Sending payment {} notification to user {}", status, userId);
    }
    
    public void sendDeliveryUpdate(Long userId, String status) {
        log.info("🚚 Sending delivery update: {} to user {}", status, userId);
    }
}
```

## RATING SERVICE

### File: rating-service/src/main/resources/application.yml
```yaml
server:
  port: 8087

spring:
  application:
    name: rating-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/rating_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### File: rating-service/src/main/java/com/fooddelivery/rating/RatingServiceApplication.java
```java
package com.fooddelivery.rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RatingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatingServiceApplication.class, args);
        System.out.println("⭐ Rating Service Started on Port 8087!");
    }
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/entity/Rating.java
```java
package com.fooddelivery.rating.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    private Long restaurantId;
    private Long deliveryPartnerId;
    
    @Column(nullable = false)
    private Double rating;
    
    private String comment;
    
    @Enumerated(EnumType.STRING)
    private RatingType ratingType;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/entity/RatingType.java
```java
package com.fooddelivery.rating.entity;

public enum RatingType {
    RESTAURANT, DELIVERY_PARTNER
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/repository/RatingRepository.java
```java
package com.fooddelivery.rating.repository;

import com.fooddelivery.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRestaurantId(Long restaurantId);
    List<Rating> findByDeliveryPartnerId(Long deliveryPartnerId);
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/dto/RatingDTO.java
```java
package com.fooddelivery.rating.dto;

import com.fooddelivery.rating.entity.RatingType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long id;
    @NotNull
    private Long userId;
    private Long restaurantId;
    private Long deliveryPartnerId;
    @NotNull
    @Min(1) @Max(5)
    private Double rating;
    private String comment;
    private RatingType ratingType;
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/service/RatingService.java
```java
package com.fooddelivery.rating.service;

import com.fooddelivery.rating.dto.RatingDTO;
import com.fooddelivery.rating.entity.Rating;
import com.fooddelivery.rating.entity.RatingType;
import com.fooddelivery.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public RatingDTO rateRestaurant(RatingDTO dto) {
        Rating rating = new Rating();
        rating.setUserId(dto.getUserId());
        rating.setRestaurantId(dto.getRestaurantId());
        rating.setRating(dto.getRating());
        rating.setComment(dto.getComment());
        rating.setRatingType(RatingType.RESTAURANT);
        
        Rating saved = ratingRepository.save(rating);
        log.info("Restaurant rated: {}", saved.getId());
        
        updateRestaurantRating(dto.getRestaurantId(), dto.getRating());
        
        return convertToDTO(saved);
    }

    private void updateRestaurantRating(Long restaurantId, Double rating) {
        String url = "http://localhost:8081/restaurants/" + restaurantId + "/rating?rating=" + rating;
        try {
            restTemplate.put(url, null);
        } catch (Exception e) {
            log.error("Failed to update restaurant rating: {}", e.getMessage());
        }
    }

    public List<RatingDTO> getRestaurantRatings(Long restaurantId) {
        return ratingRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUserId());
        dto.setRestaurantId(rating.getRestaurantId());
        dto.setDeliveryPartnerId(rating.getDeliveryPartnerId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setRatingType(rating.getRatingType());
        return dto;
    }
}
```

### File: rating-service/src/main/java/com/fooddelivery/rating/controller/RatingController.java
```java
package com.fooddelivery.rating.controller;

import com.fooddelivery.rating.dto.RatingDTO;
import com.fooddelivery.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/restaurant")
    public ResponseEntity<RatingDTO> rateRestaurant(@Valid @RequestBody RatingDTO dto) {
        return new ResponseEntity<>(ratingService.rateRestaurant(dto), HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RatingDTO>> getRestaurantRatings(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(ratingService.getRestaurantRatings(restaurantId));
    }
}
```

## WebSocket Configuration for Order Service

### File: order-service/src/main/java/com/fooddelivery/order/config/WebSocketConfig.java
```java
package com.fooddelivery.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/order-tracking").setAllowedOrigins("*").withSockJS();
    }
}
```

### File: order-service/src/main/java/com/fooddelivery/order/controller/OrderTrackingController.java
```java
package com.fooddelivery.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderTrackingController {

    @MessageMapping("/track")
    @SendTo("/topic/orders")
    public String trackOrder(String orderId) {
        return "Order " + orderId + " is being tracked";
    }
}
```

---

**COPY ALL THESE FILES TO THEIR RESPECTIVE LOCATIONS IN YOUR PROJECT!**

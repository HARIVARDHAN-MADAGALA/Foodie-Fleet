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
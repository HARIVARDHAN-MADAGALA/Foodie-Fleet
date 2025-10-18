package com.fooddelivery.order.kafka;

import com.fooddelivery.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * KAFKA PRODUCER - Publishes Order Events
 * 
 * This class sends order events to Kafka topics.
 * Other services consume these events to react to order changes.
 * 
 * How Kafka Works:
 * 1. Producer sends message to a Topic
 * 2. Kafka stores the message
 * 3. Consumers subscribed to that Topic receive the message
 * 
 * Benefits:
 * - Asynchronous communication (non-blocking)
 * - Loose coupling between services
 * - Services can be down temporarily without losing messages
 * - Multiple services can consume the same event
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String ORDER_TOPIC = "order-events";

    /**
     * PUBLISH ORDER EVENT TO KAFKA
     * 
     * @param event The order event to publish
     */
    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event to Kafka: {} for Order ID: {}", 
                event.getEventType(), event.getOrderId());
        
        try {
            kafkaTemplate.send(ORDER_TOPIC, event.getOrderId().toString(), event);
            log.info("Successfully published event to topic: {}", ORDER_TOPIC);
        } catch (Exception e) {
            log.error("Error publishing event to Kafka: {}", e.getMessage(), e);
        }
    }

    /**
     * PUBLISH ORDER CREATED EVENT
     */
    public void publishOrderCreated(OrderEvent event) {
        event.setEventType("ORDER_CREATED");
        publishOrderEvent(event);
    }

    /**
     * PUBLISH ORDER STATUS UPDATED EVENT
     */
    public void publishOrderStatusUpdated(OrderEvent event) {
        event.setEventType("ORDER_STATUS_UPDATED");
        publishOrderEvent(event);
    }

    /**
     * PUBLISH PAYMENT COMPLETED EVENT
     */
    public void publishPaymentCompleted(OrderEvent event) {
        event.setEventType("PAYMENT_COMPLETED");
        publishOrderEvent(event);
    }
}

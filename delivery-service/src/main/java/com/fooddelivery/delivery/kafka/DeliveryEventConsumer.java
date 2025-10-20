package com.fooddelivery.delivery.kafka;

import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * KAFKA CONSUMER - Delivery Service
 *
 * This service listens to Kafka events and assigns delivery partners
 * when payment is completed.
 *
 * Event Flow:
 * 1. Order Service → Publishes ORDER_CREATED
 * 2. Payment Service → Processes payment → Publishes PAYMENT_COMPLETED
 * 3. Delivery Service → Receives PAYMENT_COMPLETED → Assigns partner
 * 4. Delivery Service → Publishes DELIVERY_ASSIGNED
 *
 * This demonstrates the EVENT-DRIVEN ARCHITECTURE where services
 * react to events without knowing about each other directly.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final DeliveryService deliveryService;

    /**
     * LISTEN TO ORDER EVENTS
     *
     * This method is automatically called when an event is published
     * to the "order-events" topic.
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "delivery-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent event) {
        log.info("🎧 DELIVERY SERVICE received event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            // Only process when payment is completed
            if ("PAYMENT_COMPLETED".equals(event.getEventType())) {
                log.info("🚗 Assigning delivery partner for Order #{}", event.getOrderId());

                // Assign delivery partner
                deliveryService.assignDeliveryPartner(event);

                log.info("✅ Delivery partner assigned successfully for Order #{}",
                        event.getOrderId());
            }
            else if ("ORDER_CANCELLED".equals(event.getEventType())) {
                log.info("❌ Order cancelled: Order #{}", event.getOrderId());
                // Cancel delivery assignment if exists
                deliveryService.cancelDelivery(event.getOrderId());
            }

        } catch (Exception e) {
            log.error("❌ Error processing delivery event for Order #{}: {}",
                    event.getOrderId(), e.getMessage(), e);

            // In production:
            // 1. Retry mechanism
            // 2. Alert system
            // 3. Fallback to manual assignment
        }
    }

    /**
     * LISTEN TO DELIVERY STATUS UPDATES
     *
     * Optional: Listen to delivery-events topic for self-monitoring
     */
    @KafkaListener(
            topics = "delivery-events",
            groupId = "delivery-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDeliveryEvent(OrderEvent event) {
        log.info("🎧 DELIVERY SERVICE monitoring event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            if ("DELIVERY_COMPLETED".equals(event.getEventType())) {
                log.info("✅ Delivery completed for Order #{}", event.getOrderId());
                // Mark delivery partner as available
                deliveryService.markPartnerAvailable(event.getDeliveryPartnerId());
            }
            else if ("DELIVERY_FAILED".equals(event.getEventType())) {
                log.info("⚠️ Delivery failed for Order #{}", event.getOrderId());
                // Reassign to another partner
                deliveryService.reassignDeliveryPartner(event.getOrderId());
            }

        } catch (Exception e) {
            log.error("❌ Error processing delivery status event: {}", e.getMessage(), e);
        }
    }
}

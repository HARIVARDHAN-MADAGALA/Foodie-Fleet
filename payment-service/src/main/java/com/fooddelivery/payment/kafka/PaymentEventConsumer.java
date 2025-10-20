package com.fooddelivery.payment.kafka;

import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * KAFKA CONSUMER - Payment Service
 *
 * This class LISTENS to Kafka topics and consumes order events.
 * When a new order is placed, this service processes the payment.
 *
 * How it works:
 * 1. Order Service publishes "ORDER_CREATED" event to Kafka
 * 2. This consumer receives the event automatically
 * 3. Payment is processed
 * 4. Payment status is updated
 *
 * Benefits of Kafka Consumer:
 * - Asynchronous processing (doesn't block Order Service)
 * - Automatic retry on failure
 * - Can process events at its own pace
 * - Scalable (multiple consumers can process in parallel)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    /**
     * LISTEN TO ORDER EVENTS FROM KAFKA
     *
     * The @KafkaListener annotation automatically subscribes to the "order-events" topic.
     * Whenever a message is published to this topic, this method is called automatically.
     *
     * @param event The order event received from Kafka
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "payment-service-group"
    )
    public void consumeOrderEvent(OrderEvent event) {
        log.info("🎧 PAYMENT SERVICE received event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            // Check event type and process accordingly
            if ("ORDER_CREATED".equals(event.getEventType())) {
                log.info("💳 Processing payment for Order #{}", event.getOrderId());

                // Process payment through Payment Service
                paymentService.processPayment(event);

                log.info("✅ Payment processed successfully for Order #{}", event.getOrderId());
            }
            else if ("ORDER_UPDATED".equals(event.getEventType())) {
                log.info("📝 Order updated: Order #{}", event.getOrderId());
                // Handle order updates if needed
            }

        } catch (Exception e) {
            log.error("❌ Error processing payment event for Order #{}: {}",
                    event.getOrderId(), e.getMessage(), e);

            // In production, you would:
            // 1. Send to Dead Letter Queue (DLQ)
            // 2. Trigger retry mechanism
            // 3. Send alert/notification
        }
    }

    /**
     * LISTEN TO DELIVERY EVENTS
     *
     * Payment service can also listen to delivery events
     * to update payment status or handle refunds
     */
    @KafkaListener(
            topics = "delivery-events",
            groupId = "payment-service-group"
    )
    public void consumeDeliveryEvent(OrderEvent event) {
        log.info("🎧 PAYMENT SERVICE received delivery event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            if ("DELIVERY_FAILED".equals(event.getEventType())) {
                log.info("🔄 Processing refund for failed delivery: Order #{}", event.getOrderId());
                // Process refund logic here
                paymentService.processRefund(event.getOrderId());
            }
        } catch (Exception e) {
            log.error("❌ Error processing delivery event: {}", e.getMessage(), e);
        }
    }
}

package com.fooddelivery.notification.kafka;

import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * KAFKA CONSUMER - Notification Service
 *
 * This service listens to ALL Kafka topics and sends notifications
 * (email, SMS, push notifications) based on events.
 *
 * Why Notification Service listens to multiple topics:
 * - It needs to know about ALL events in the system
 * - Sends notifications for order creation, payment, delivery, etc.
 * - Acts as a centralized notification hub
 *
 * Event Types Handled:
 * 1. ORDER_CREATED → "Your order has been placed!"
 * 2. PAYMENT_COMPLETED → "Payment successful!"
 * 3. DELIVERY_ASSIGNED → "Your order is being prepared!"
 * 4. DELIVERY_COMPLETED → "Your order has been delivered!"
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    /**
     * LISTEN TO ORDER EVENTS
     *
     * Receives all order-related events and sends appropriate notifications
     */
    @KafkaListener(
            topics = "order-events",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent event) {
        log.info("🎧 NOTIFICATION SERVICE received ORDER event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    log.info("📧 Sending 'Order Placed' notification for Order #{}", event.getOrderId());
                    notificationService.sendOrderPlacedNotification(event);
                    break;

                case "ORDER_UPDATED":
                    log.info("📧 Sending 'Order Updated' notification for Order #{}", event.getOrderId());
                    notificationService.sendOrderUpdatedNotification(event);
                    break;

                case "ORDER_CANCELLED":
                    log.info("📧 Sending 'Order Cancelled' notification for Order #{}", event.getOrderId());
                    notificationService.sendOrderCancelledNotification(event);
                    break;

                default:
                    log.info("📝 Received order event: {}", event.getEventType());
            }

            log.info("✅ Notification sent successfully for Order #{}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Error sending notification for Order #{}: {}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }

    /**
     * LISTEN TO PAYMENT EVENTS
     *
     * Sends notifications when payment is processed
     *
     * NOTE: Temporarily disabled for initial startup. Enable after Kafka topics are created.
     */
    // @KafkaListener(
    //         topics = "payment-events",
    //         groupId = "notification-service-group",
    //         containerFactory = "kafkaListenerContainerFactory"
    // )
    public void consumePaymentEvent(OrderEvent event) {
        log.info("🎧 NOTIFICATION SERVICE received PAYMENT event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            if ("PAYMENT_COMPLETED".equals(event.getEventType())) {
                log.info("💳 Sending 'Payment Successful' notification for Order #{}",
                        event.getOrderId());
                notificationService.sendPaymentSuccessNotification(event);
            }
            else if ("PAYMENT_FAILED".equals(event.getEventType())) {
                log.info("❌ Sending 'Payment Failed' notification for Order #{}",
                        event.getOrderId());
                notificationService.sendPaymentFailedNotification(event);
            }

            log.info("✅ Payment notification sent for Order #{}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Error sending payment notification: {}", e.getMessage(), e);
        }
    }

    /**
     * LISTEN TO DELIVERY EVENTS
     *
     * Sends notifications about delivery status
     *
     * NOTE: Temporarily disabled for initial startup. Enable after Kafka topics are created.
     */
    // @KafkaListener(
    //         topics = "delivery-events",
    //         groupId = "notification-service-group",
    //         containerFactory = "kafkaListenerContainerFactory"
    // )
    public void consumeDeliveryEvent(OrderEvent event) {
        log.info("🎧 NOTIFICATION SERVICE received DELIVERY event: {} for Order ID: {}",
                event.getEventType(), event.getOrderId());

        try {
            switch (event.getEventType()) {
                case "DELIVERY_ASSIGNED":
                    log.info("🚗 Sending 'Delivery Assigned' notification for Order #{}",
                            event.getOrderId());
                    notificationService.sendDeliveryAssignedNotification(event);
                    break;

                case "DELIVERY_PICKED_UP":
                    log.info("📦 Sending 'Order Picked Up' notification for Order #{}",
                            event.getOrderId());
                    notificationService.sendOrderPickedUpNotification(event);
                    break;

                case "DELIVERY_COMPLETED":
                    log.info("✅ Sending 'Order Delivered' notification for Order #{}",
                            event.getOrderId());
                    notificationService.sendOrderDeliveredNotification(event);
                    break;

                case "DELIVERY_FAILED":
                    log.info("⚠️ Sending 'Delivery Failed' notification for Order #{}",
                            event.getOrderId());
                    notificationService.sendDeliveryFailedNotification(event);
                    break;

                default:
                    log.info("📝 Received delivery event: {}", event.getEventType());
            }

            log.info("✅ Delivery notification sent for Order #{}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Error sending delivery notification: {}", e.getMessage(), e);
        }
    }

    /**
     * LISTEN TO RATING EVENTS (Optional)
     *
     * Sends thank you messages when users rate orders
     *
     * NOTE: Temporarily disabled for initial startup. Enable after Kafka topics are created.
     */
    // @KafkaListener(
    //         topics = "rating-events",
    //         groupId = "notification-service-group",
    //         containerFactory = "kafkaListenerContainerFactory"
    // )
    public void consumeRatingEvent(OrderEvent event) {
        log.info("🎧 NOTIFICATION SERVICE received RATING event for Order ID: {}",
                event.getOrderId());

        try {
            log.info("⭐ Sending 'Thank You for Rating' notification for Order #{}",
                    event.getOrderId());
            notificationService.sendRatingThankYouNotification(event);

            log.info("✅ Rating notification sent for Order #{}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Error sending rating notification: {}", e.getMessage(), e);
        }
    }
}

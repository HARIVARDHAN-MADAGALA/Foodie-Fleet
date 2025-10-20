package com.fooddelivery.notification.service;

import com.fooddelivery.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * NOTIFICATION SERVICE - Business Logic
 *
 * Sends notifications to users based on various events.
 * In a real application, this would integrate with:
 * - Email services (SendGrid, AWS SES)
 * - SMS services (Twilio, AWS SNS)
 * - Push notification services (Firebase, OneSignal)
 *
 * For this learning project, we simulate notifications with logs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /**
     * ORDER PLACED NOTIFICATION
     */
    public void sendOrderPlacedNotification(OrderEvent event) {
        String message = String.format(
                "Dear Customer,\n" +
                        "Your order #%d has been placed successfully!\n" +
                        "Total Amount: $%.2f\n" +
                        "We'll notify you once payment is confirmed.",
                event.getOrderId(), event.getTotalAmount());

        sendNotification(event.getUserId(), "Order Placed", message);
    }

    /**
     * ORDER UPDATED NOTIFICATION
     */
    public void sendOrderUpdatedNotification(OrderEvent event) {
        String message = String.format(
                "Your order #%d status has been updated to: %s",
                event.getOrderId(), event.getOrderStatus());

        sendNotification(event.getUserId(), "Order Updated", message);
    }

    /**
     * ORDER CANCELLED NOTIFICATION
     */
    public void sendOrderCancelledNotification(OrderEvent event) {
        String message = String.format(
                "Your order #%d has been cancelled.\n" +
                        "If you were charged, you'll receive a refund within 3-5 business days.",
                event.getOrderId());

        sendNotification(event.getUserId(), "Order Cancelled", message);
    }

    /**
     * PAYMENT SUCCESS NOTIFICATION
     */
    public void sendPaymentSuccessNotification(OrderEvent event) {
        String message = String.format(
                "Payment of $%.2f for order #%d has been processed successfully!\n" +
                        "Your order is being prepared.",
                event.getTotalAmount(), event.getOrderId());

        sendNotification(event.getUserId(), "Payment Successful", message);
    }

    /**
     * PAYMENT FAILED NOTIFICATION
     */
    public void sendPaymentFailedNotification(OrderEvent event) {
        String message = String.format(
                "Payment for order #%d failed.\n" +
                        "Please try again with a different payment method.",
                event.getOrderId());

        sendNotification(event.getUserId(), "Payment Failed", message);
    }

    /**
     * DELIVERY ASSIGNED NOTIFICATION
     */
    public void sendDeliveryAssignedNotification(OrderEvent event) {
        String message = String.format(
                "Great news! A delivery partner has been assigned to your order #%d.\n" +
                        "Your order is being prepared and will be delivered soon!",
                event.getOrderId());

        sendNotification(event.getUserId(), "Delivery Partner Assigned", message);
    }

    /**
     * ORDER PICKED UP NOTIFICATION
     */
    public void sendOrderPickedUpNotification(OrderEvent event) {
        String message = String.format(
                "Your order #%d has been picked up by the delivery partner.\n" +
                        "Track your order in real-time!",
                event.getOrderId());

        sendNotification(event.getUserId(), "Order Picked Up", message);
    }

    /**
     * ORDER DELIVERED NOTIFICATION
     */
    public void sendOrderDeliveredNotification(OrderEvent event) {
        String message = String.format(
                "Your order #%d has been delivered successfully!\n" +
                        "Enjoy your meal! 🎉\n" +
                        "Please rate your experience.",
                event.getOrderId());

        sendNotification(event.getUserId(), "Order Delivered", message);
    }

    /**
     * DELIVERY FAILED NOTIFICATION
     */
    public void sendDeliveryFailedNotification(OrderEvent event) {
        String message = String.format(
                "We're sorry! Delivery of order #%d failed.\n" +
                        "We're assigning a new delivery partner. You'll be refunded if needed.",
                event.getOrderId());

        sendNotification(event.getUserId(), "Delivery Issue", message);
    }

    /**
     * RATING THANK YOU NOTIFICATION
     */
    public void sendRatingThankYouNotification(OrderEvent event) {
        String message = String.format(
                "Thank you for rating order #%d!\n" +
                        "Your feedback helps us improve our service. 🙏",
                event.getOrderId());

        sendNotification(event.getUserId(), "Thank You", message);
    }

    /**
     * SEND NOTIFICATION (Simulated)
     *
     * In production, this would call external services
     */
    private void sendNotification(Long userId, String subject, String message) {
        log.info("=" .repeat(80));
        log.info("📧 EMAIL NOTIFICATION");
        log.info("To: User ID {}", userId);
        log.info("Subject: {}", subject);
        log.info("Message:\n{}", message);
        log.info("=" .repeat(80));

        log.info("📱 PUSH NOTIFICATION sent to User ID: {}", userId);
        log.info("💬 SMS NOTIFICATION sent to User ID: {}", userId);
    }
}

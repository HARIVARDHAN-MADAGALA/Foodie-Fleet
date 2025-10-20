package com.fooddelivery.payment.service;

import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.payment.dto.PaymentDTO;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentMethod;
import com.fooddelivery.payment.entity.PaymentStatus;
import com.fooddelivery.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * PAYMENT SERVICE - Business Logic
 *
 * Handles all payment-related operations:
 * - Process payments from Kafka events
 * - Simulate payment gateway calls
 * - Handle refunds
 * - Publish payment events
 */
@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private final Random random = new Random();

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * PROCESS PAYMENT (Called from Kafka Consumer)
     *
     * This method is triggered when an ORDER_CREATED event is received.
     * It simulates payment gateway processing.
     */
    @Transactional
    public PaymentDTO processPayment(OrderEvent event) {
        log.info("💳 Processing payment for Order ID: {}", event.getOrderId());

        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderId(event.getOrderId())) {
            log.warn("⚠️ Payment already exists for Order ID: {}", event.getOrderId());
            return convertToDTO(paymentRepository.findByOrderId(event.getOrderId()).get());
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setUserId(event.getUserId());
        payment.setAmount(event.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Default method
        payment.setStatus(PaymentStatus.PROCESSING);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("💾 Payment record created with ID: {}", savedPayment.getId());

        // Simulate payment gateway call (2-3 seconds delay)
        try {
            log.info("🔄 Calling payment gateway...");
            Thread.sleep(2000 + random.nextInt(1000)); // 2-3 seconds

            // Simulate 95% success rate
            boolean paymentSuccess = random.nextInt(100) < 95;

            if (paymentSuccess) {
                // Payment successful
                savedPayment.setStatus(PaymentStatus.SUCCESS);
                savedPayment.setTransactionId("TXN" + System.currentTimeMillis());
                savedPayment.setGatewayResponse("Payment processed successfully");
                savedPayment.setCompletedAt(LocalDateTime.now());

                log.info("✅ Payment successful! Transaction ID: {}", savedPayment.getTransactionId());

                // Publish PAYMENT_COMPLETED event to Kafka
                if (kafkaTemplate != null) {
                    publishPaymentEvent(event, "PAYMENT_COMPLETED");
                } else {
                    log.warn("⚠️ Kafka not configured - event not published");
                }

            } else {
                // Payment failed
                savedPayment.setStatus(PaymentStatus.FAILED);
                savedPayment.setGatewayResponse("Insufficient funds");
                savedPayment.setCompletedAt(LocalDateTime.now());

                log.error("❌ Payment failed for Order ID: {}", event.getOrderId());

                // Publish PAYMENT_FAILED event
                if (kafkaTemplate != null) {
                    publishPaymentEvent(event, "PAYMENT_FAILED");
                } else {
                    log.warn("⚠️ Kafka not configured - event not published");
                }
            }

            paymentRepository.save(savedPayment);

        } catch (InterruptedException e) {
            log.error("Error during payment processing: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

        return convertToDTO(savedPayment);
    }

    /**
     * PROCESS REFUND
     *
     * Handles refund when delivery fails or order is cancelled
     */
    @Transactional
    public PaymentDTO processRefund(Long orderId) {
        log.info("🔄 Processing refund for Order ID: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Cannot refund payment that is not successful");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse("Refund processed");
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("✅ Refund processed successfully for Order ID: {}", orderId);

        // Publish REFUND_COMPLETED event
        if (kafkaTemplate != null) {
            OrderEvent refundEvent = new OrderEvent();
            refundEvent.setOrderId(orderId);
            refundEvent.setEventType("REFUND_COMPLETED");
            refundEvent.setTimestamp(LocalDateTime.now());
            kafkaTemplate.send("payment-events", refundEvent);
        } else {
            log.warn("⚠️ Kafka not configured - refund event not published");
        }

        return convertToDTO(refundedPayment);
    }

    /**
     * GET PAYMENT BY ORDER ID
     */
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return convertToDTO(payment);
    }

    /**
     * GET ALL PAYMENTS BY USER
     */
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * PUBLISH PAYMENT EVENT TO KAFKA
     */
    private void publishPaymentEvent(OrderEvent originalEvent, String eventType) {
        OrderEvent paymentEvent = new OrderEvent();
        paymentEvent.setOrderId(originalEvent.getOrderId());
        paymentEvent.setUserId(originalEvent.getUserId());
        paymentEvent.setRestaurantId(originalEvent.getRestaurantId());
        paymentEvent.setTotalAmount(originalEvent.getTotalAmount());
        paymentEvent.setEventType(eventType);
        paymentEvent.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send("payment-events", paymentEvent);
        log.info("📨 Published {} event to Kafka for Order ID: {}", eventType, originalEvent.getOrderId());
    }

    /**
     * CONVERT ENTITY TO DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setStatus(payment.getStatus().name());
        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayResponse(payment.getGatewayResponse());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setCompletedAt(payment.getCompletedAt());
        return dto;
    }
}

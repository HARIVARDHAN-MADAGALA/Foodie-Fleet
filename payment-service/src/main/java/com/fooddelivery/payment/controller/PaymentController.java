package com.fooddelivery.payment.controller;

import com.fooddelivery.payment.dto.PaymentDTO;
import com.fooddelivery.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PAYMENT CONTROLLER - REST API
 *
 * Endpoints:
 * - GET /api/payments/order/{orderId} - Get payment by order ID
 * - GET /api/payments/user/{userId} - Get all payments by user
 * - POST /api/payments/{orderId}/refund - Process refund
 *
 * Note: Payment creation is handled automatically via Kafka events,
 * so there's no POST endpoint for creating payments.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * GET PAYMENT BY ORDER ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("📥 GET /api/payments/order/{}", orderId);
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    /**
     * GET ALL PAYMENTS BY USER
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable Long userId) {
        log.info("📥 GET /api/payments/user/{}", userId);
        List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * PROCESS REFUND
     */
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<PaymentDTO> processRefund(@PathVariable Long orderId) {
        log.info("📥 POST /api/payments/{}/refund", orderId);
        PaymentDTO refundedPayment = paymentService.processRefund(orderId);
        return ResponseEntity.ok(refundedPayment);
    }
}

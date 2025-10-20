package com.fooddelivery.payment.repository;

import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PAYMENT REPOSITORY
 *
 * Data access layer for Payment entity.
 * Provides methods to query payment records.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by order ID
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Find all payments by user
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Find all payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Check if payment exists for order
     */
    boolean existsByOrderId(Long orderId);
}

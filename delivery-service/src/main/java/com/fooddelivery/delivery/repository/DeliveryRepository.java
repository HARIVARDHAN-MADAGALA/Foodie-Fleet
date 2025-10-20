package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DELIVERY REPOSITORY
 *
 * Data access layer for Delivery entity
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Find delivery by order ID
     */
    Optional<Delivery> findByOrderId(Long orderId);

    /**
     * Find all deliveries by partner
     */
    List<Delivery> findByPartnerId(Long partnerId);

    /**
     * Find all deliveries by status
     */
    List<Delivery> findByStatus(DeliveryStatus status);

    /**
     * Find active deliveries by partner
     */
    List<Delivery> findByPartnerIdAndStatusIn(Long partnerId, List<DeliveryStatus> statuses);

    /**
     * Check if delivery exists for order
     */
    boolean existsByOrderId(Long orderId);
}

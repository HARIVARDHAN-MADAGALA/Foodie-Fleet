package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.dto.DeliveryDTO;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import com.fooddelivery.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DELIVERY CONTROLLER - REST API
 *
 * Endpoints:
 * - GET /api/deliveries/order/{orderId} - Get delivery by order ID
 * - GET /api/deliveries/partner/{partnerId} - Get all deliveries by partner
 * - PUT /api/deliveries/{id}/status - Update delivery status
 * - POST /api/deliveries/{orderId}/reassign - Reassign delivery partner
 */
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * GET DELIVERY BY ORDER ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        log.info("📥 GET /api/deliveries/order/{}", orderId);
        DeliveryDTO delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    /**
     * GET ALL DELIVERIES BY PARTNER
     */
    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<DeliveryDTO>> getDeliveriesByPartnerId(@PathVariable Long partnerId) {
        log.info("📥 GET /api/deliveries/partner/{}", partnerId);
        List<DeliveryDTO> deliveries = deliveryService.getDeliveriesByPartnerId(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * UPDATE DELIVERY STATUS
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("📥 PUT /api/deliveries/{}/status - New status: {}", id, status);
        DeliveryStatus newStatus = DeliveryStatus.valueOf(status);
        DeliveryDTO updatedDelivery = deliveryService.updateDeliveryStatus(id, newStatus);
        return ResponseEntity.ok(updatedDelivery);
    }

    /**
     * REASSIGN DELIVERY PARTNER
     */
    @PostMapping("/{orderId}/reassign")
    public ResponseEntity<DeliveryDTO> reassignDeliveryPartner(@PathVariable Long orderId) {
        log.info("📥 POST /api/deliveries/{}/reassign", orderId);
        DeliveryDTO reassignedDelivery = deliveryService.reassignDeliveryPartner(orderId);
        return ResponseEntity.ok(reassignedDelivery);
    }
}

package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.dto.DeliveryDTO;
import com.fooddelivery.delivery.entity.*;
import com.fooddelivery.delivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.delivery.repository.DeliveryRepository;
import com.fooddelivery.order.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DELIVERY SERVICE - Business Logic
 *
 * Handles delivery partner assignment and tracking
 */
@Service
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPartnerRepository partnerRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           DeliveryPartnerRepository partnerRepository) {
        this.deliveryRepository = deliveryRepository;
        this.partnerRepository = partnerRepository;
    }

    /**
     * ASSIGN DELIVERY PARTNER (Called from Kafka Consumer)
     *
     * Automatically assigns an available partner when payment is completed
     */
    @Transactional
    public DeliveryDTO assignDeliveryPartner(OrderEvent event) {
        log.info("🚗 Assigning delivery partner for Order ID: {}", event.getOrderId());

        // Check if delivery already assigned
        if (deliveryRepository.existsByOrderId(event.getOrderId())) {
            log.warn("⚠️ Delivery already assigned for Order ID: {}", event.getOrderId());
            return convertToDTO(deliveryRepository.findByOrderId(event.getOrderId()).get());
        }

        // Find available delivery partner
        List<DeliveryPartner> availablePartners = partnerRepository.findByStatus(PartnerStatus.AVAILABLE);
        if (availablePartners.isEmpty()) {
            throw new RuntimeException("No available delivery partners found");
        }
        DeliveryPartner partner = availablePartners.get(0);

        // Create delivery assignment
        Delivery delivery = new Delivery();
        delivery.setOrderId(event.getOrderId());
        delivery.setPartnerId(partner.getId());
        delivery.setRestaurantId(event.getRestaurantId());
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        delivery.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));

        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Mark partner as busy
        partner.setStatus(PartnerStatus.BUSY);
        partnerRepository.save(partner);

        log.info("✅ Delivery partner {} assigned to Order ID: {}",
                partner.getName(), event.getOrderId());

        // Publish DELIVERY_ASSIGNED event
        publishDeliveryEvent(event, "DELIVERY_ASSIGNED", partner.getId());

        return convertToDTO(savedDelivery);
    }

    /**
     * MARK PARTNER AS AVAILABLE
     */
    @Transactional
    public void markPartnerAvailable(Long partnerId) {
        log.info("✅ Marking partner {} as available", partnerId);
        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found: " + partnerId));
        partner.setStatus(PartnerStatus.AVAILABLE);
        partnerRepository.save(partner);
    }

    /**
     * UPDATE DELIVERY STATUS
     */
    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        log.info("📝 Updating delivery {} to status: {}", deliveryId, newStatus);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        delivery.setStatus(newStatus);

        switch (newStatus) {
            case PICKED_UP:
                delivery.setPickedUpAt(LocalDateTime.now());
                break;
            case DELIVERED:
                delivery.setDeliveredAt(LocalDateTime.now());
                // Mark partner as available
                markPartnerAvailable(delivery.getPartnerId());
                break;
            case FAILED:
            case CANCELLED:
                // Mark partner as available
                markPartnerAvailable(delivery.getPartnerId());
                break;
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Publish event
        if (kafkaTemplate != null) {
            OrderEvent event = new OrderEvent();
            event.setOrderId(delivery.getOrderId());
            event.setDeliveryPartnerId(delivery.getPartnerId());
            event.setEventType("DELIVERY_" + newStatus.name());
            event.setTimestamp(LocalDateTime.now());
            kafkaTemplate.send("delivery-events", event);
        } else {
            log.warn("⚠️ Kafka not configured - delivery event not published");
        }

        return convertToDTO(updatedDelivery);
    }

    /**
     * CANCEL DELIVERY
     */
    @Transactional
    public void cancelDelivery(Long orderId) {
        log.info("❌ Cancelling delivery for Order ID: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElse(null);

        if (delivery != null) {
            delivery.setStatus(DeliveryStatus.CANCELLED);
            deliveryRepository.save(delivery);

            // Free up partner
            markPartnerAvailable(delivery.getPartnerId());
        }
    }

    /**
     * REASSIGN DELIVERY PARTNER
     */
    @Transactional
    public DeliveryDTO reassignDeliveryPartner(Long orderId) {
        log.info("🔄 Reassigning delivery partner for Order ID: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        // Free up current partner
        markPartnerAvailable(delivery.getPartnerId());

        // Find new available partner
        List<DeliveryPartner> availablePartners = partnerRepository.findByStatus(PartnerStatus.AVAILABLE);
        if (availablePartners.isEmpty()) {
            throw new RuntimeException("No available delivery partners found");
        }
        DeliveryPartner newPartner = availablePartners.get(0);

        delivery.setPartnerId(newPartner.getId());
        delivery.setAssignedAt(LocalDateTime.now());

        Delivery reassignedDelivery = deliveryRepository.save(delivery);

        // Mark new partner as busy
        newPartner.setStatus(PartnerStatus.BUSY);
        partnerRepository.save(newPartner);

        log.info("✅ Delivery reassigned to partner: {}", newPartner.getName());

        return convertToDTO(reassignedDelivery);
    }

    /**
     * GET DELIVERY BY ORDER ID
     */
    public DeliveryDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
        return convertToDTO(delivery);
    }

    /**
     * GET ALL DELIVERIES BY PARTNER
     */
    public List<DeliveryDTO> getDeliveriesByPartnerId(Long partnerId) {
        return deliveryRepository.findByPartnerId(partnerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * PUBLISH DELIVERY EVENT TO KAFKA
     */
    private void publishDeliveryEvent(OrderEvent originalEvent, String eventType, Long partnerId) {
        if (kafkaTemplate != null) {
            OrderEvent deliveryEvent = new OrderEvent();
            deliveryEvent.setOrderId(originalEvent.getOrderId());
            deliveryEvent.setUserId(originalEvent.getUserId());
            deliveryEvent.setRestaurantId(originalEvent.getRestaurantId());
            deliveryEvent.setDeliveryPartnerId(partnerId);
            deliveryEvent.setEventType(eventType);
            deliveryEvent.setTimestamp(LocalDateTime.now());

            kafkaTemplate.send("delivery-events", deliveryEvent);
            log.info("📨 Published {} event to Kafka", eventType);
        } else {
            log.warn("⚠️ Kafka not configured - {} event not published", eventType);
        }
    }

    /**
     * CONVERT ENTITY TO DTO
     */
    private DeliveryDTO convertToDTO(Delivery delivery) {
        DeliveryDTO dto = new DeliveryDTO();
        dto.setId(delivery.getId());
        dto.setOrderId(delivery.getOrderId());
        dto.setPartnerId(delivery.getPartnerId());
        dto.setRestaurantId(delivery.getRestaurantId());
        dto.setPickupAddress(delivery.getPickupAddress());
        dto.setDeliveryAddress(delivery.getDeliveryAddress());
        dto.setStatus(delivery.getStatus().name());
        dto.setAssignedAt(delivery.getAssignedAt());
        dto.setPickedUpAt(delivery.getPickedUpAt());
        dto.setDeliveredAt(delivery.getDeliveredAt());
        dto.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        dto.setNotes(delivery.getNotes());
        return dto;
    }
}

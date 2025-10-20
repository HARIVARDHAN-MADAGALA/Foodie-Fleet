package com.fooddelivery.order.service;

import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.OrderDTO;
import com.fooddelivery.order.dto.OrderItemDTO;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.PaymentStatus;
import com.fooddelivery.order.event.OrderEvent;
import com.fooddelivery.order.kafka.OrderEventProducer;
import com.fooddelivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ORDER SERVICE LAYER
 *
 * Handles business logic for:
 * - Order creation and management with Circuit Breaker protection
 * - Order status updates
 * - Publishing events to Kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final RestaurantClient restaurantClient;

    /**
     * CREATE NEW ORDER WITH CIRCUIT BREAKER PROTECTION
     *
     * Flow:
     * 1. Validate restaurant availability via Circuit Breaker protected call
     * 2. Create order in database
     * 3. Publish ORDER_CREATED event to Kafka
     * 4. Payment Service listens to this event and processes payment
     * 5. Notification Service sends order confirmation
     *
     * Circuit Breaker protects against Restaurant Service failures
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating new order for user ID: {}", orderDTO.getUserId());

        // CIRCUIT BREAKER PROTECTED CALL
        // Validate restaurant is available before creating order
        log.info("🔵 Validating restaurant availability with Circuit Breaker...");
        RestaurantClient.RestaurantDTO restaurant = restaurantClient.getRestaurant(orderDTO.getRestaurantId());

        if (restaurant == null || !restaurant.getAvailable()) {
            log.error("❌ Restaurant {} is not available", orderDTO.getRestaurantId());
            throw new RuntimeException("Restaurant is not available for orders");
        }

        log.info("✅ Restaurant validated: {}", restaurant.getName());

        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setRestaurantId(orderDTO.getRestaurantId());
        order.setAddressId(orderDTO.getAddressId());
        order.setSpecialInstructions(orderDTO.getSpecialInstructions());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderTime(LocalDateTime.now());

        double total = 0.0;
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setMenuItemId(itemDTO.getMenuItemId());
            item.setItemName(itemDTO.getItemName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setSubtotal(itemDTO.getPrice() * itemDTO.getQuantity());
            item.setOrder(order);
            order.getItems().add(item);
            total += item.getSubtotal();
        }

        order.setTotalAmount(total);
        order.setDeliveryFee(orderDTO.getDeliveryFee() != null ? orderDTO.getDeliveryFee() : 50.0);
        order.setDiscount(orderDTO.getDiscount() != null ? orderDTO.getDiscount() : 0.0);
        order.setFinalAmount(total + order.getDeliveryFee() - order.getDiscount());

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        OrderEvent event = new OrderEvent();
        event.setOrderId(savedOrder.getId());
        event.setUserId(savedOrder.getUserId());
        event.setRestaurantId(savedOrder.getRestaurantId());
        event.setTotalAmount(savedOrder.getFinalAmount());
        event.setOrderStatus(savedOrder.getStatus());
        event.setPaymentStatus(savedOrder.getPaymentStatus());
        event.setTimestamp(LocalDateTime.now());

        orderEventProducer.publishOrderCreated(event);

        return convertToDTO(savedOrder);
    }

    /**
     * GET ORDER BY ID
     */
    public OrderDTO getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        return convertToDTO(order);
    }

    /**
     * GET ALL ORDERS
     */
    public List<OrderDTO> getAllOrders() {
        log.info("Fetching all orders");

        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET USER ORDER HISTORY
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        log.info("Fetching orders for user ID: {}", userId);

        return orderRepository.findByUserIdOrderByOrderTimeDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE ORDER STATUS
     *
     * Publishes status update event to Kafka
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveryTime(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent();
        event.setOrderId(updatedOrder.getId());
        event.setUserId(updatedOrder.getUserId());
        event.setRestaurantId(updatedOrder.getRestaurantId());
        event.setDeliveryPartnerId(updatedOrder.getDeliveryPartnerId());
        event.setOrderStatus(updatedOrder.getStatus());
        event.setPaymentStatus(updatedOrder.getPaymentStatus());
        event.setTimestamp(LocalDateTime.now());

        orderEventProducer.publishOrderStatusUpdated(event);

        return convertToDTO(updatedOrder);
    }

    /**
     * UPDATE PAYMENT STATUS
     * Called by Payment Service after payment processing
     */
    @Transactional
    public OrderDTO updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        log.info("Updating payment status for order {} to: {}", orderId, paymentStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(paymentStatus);

        if (paymentStatus == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else if (paymentStatus == PaymentStatus.FAILED) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        Order updatedOrder = orderRepository.save(order);

        if (paymentStatus == PaymentStatus.COMPLETED) {
            OrderEvent event = new OrderEvent();
            event.setOrderId(updatedOrder.getId());
            event.setUserId(updatedOrder.getUserId());
            event.setRestaurantId(updatedOrder.getRestaurantId());
            event.setTotalAmount(updatedOrder.getFinalAmount());
            event.setOrderStatus(updatedOrder.getStatus());
            event.setPaymentStatus(updatedOrder.getPaymentStatus());
            event.setTimestamp(LocalDateTime.now());

            orderEventProducer.publishPaymentCompleted(event);
        }

        return convertToDTO(updatedOrder);
    }

    /**
     * ASSIGN DELIVERY PARTNER
     * Called by Delivery Service
     */
    @Transactional
    public OrderDTO assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        log.info("Assigning delivery partner {} to order {}", deliveryPartnerId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDeliveryPartnerId(deliveryPartnerId);
        order.setStatus(OrderStatus.READY);

        Order updatedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent();
        event.setOrderId(updatedOrder.getId());
        event.setUserId(updatedOrder.getUserId());
        event.setDeliveryPartnerId(deliveryPartnerId);
        event.setOrderStatus(updatedOrder.getStatus());
        event.setEventType("DELIVERY_ASSIGNED");
        event.setTimestamp(LocalDateTime.now());

        orderEventProducer.publishOrderEvent(event);

        return convertToDTO(updatedOrder);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setRestaurantId(order.getRestaurantId());
        dto.setDeliveryPartnerId(order.getDeliveryPartnerId());
        dto.setAddressId(order.getAddressId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setDiscount(order.getDiscount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setSpecialInstructions(order.getSpecialInstructions());
        dto.setOrderTime(order.getOrderTime());
        dto.setDeliveryTime(order.getDeliveryTime());

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setMenuItemId(item.getMenuItemId());
                    itemDTO.setItemName(item.getItemName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setSubtotal(item.getSubtotal());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}

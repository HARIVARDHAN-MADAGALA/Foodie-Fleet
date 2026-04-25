package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderDTO;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.PaymentStatus;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ORDER REST CONTROLLER
 *
 * Handles all order-related HTTP requests with role-based security
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * CREATE NEW ORDER
     * POST /orders
     * Role: CUSTOMER only
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        if (!orderDTO.getUserId().equals(currentUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * GET ORDER BY ID
     * GET /orders/{id}
     * Role: CUSTOMER, RESTAURANT, DELIVERY, ADMIN
     */
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT', 'DELIVERY', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * GET ALL ORDERS
     * GET /orders
     * Role: ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET USER ORDER HISTORY
     * GET /orders/user/{userId}
     * Role: CUSTOMER (own orders only), ADMIN
     */
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        if (!currentUserId.equals(userId) && !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<OrderDTO> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * UPDATE ORDER STATUS
     * PUT /orders/{id}/status
     * Role: RESTAURANT, ADMIN
     */
    @PreAuthorize("hasAnyRole('RESTAURANT', 'ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * UPDATE PAYMENT STATUS
     * PUT /orders/{id}/payment-status
     * Internal endpoint called by Payment Service
     * Role: ADMIN (for internal service calls)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus paymentStatus) {
        OrderDTO updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * ASSIGN DELIVERY PARTNER
     * PUT /orders/{id}/assign-delivery
     * Internal endpoint called by Delivery Service
     * Role: DELIVERY, ADMIN
     */
    @PreAuthorize("hasAnyRole('DELIVERY', 'ADMIN')")
    @PutMapping("/{id}/assign-delivery")
    public ResponseEntity<OrderDTO> assignDeliveryPartner(
            @PathVariable Long id,
            @RequestParam Long deliveryPartnerId) {
        OrderDTO updatedOrder = orderService.assignDeliveryPartner(id, deliveryPartnerId);
        return ResponseEntity.ok(updatedOrder);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

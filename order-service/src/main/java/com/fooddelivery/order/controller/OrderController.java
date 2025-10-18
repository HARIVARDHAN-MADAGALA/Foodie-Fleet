package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderDTO;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.entity.PaymentStatus;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ORDER REST CONTROLLER
 * 
 * Handles all order-related HTTP requests
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * CREATE NEW ORDER
     * POST /orders
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * GET ORDER BY ID
     * GET /orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * GET ALL ORDERS
     * GET /orders
     */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET USER ORDER HISTORY
     * GET /orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * UPDATE ORDER STATUS
     * PUT /orders/{id}/status
     */
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
     */
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
     */
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

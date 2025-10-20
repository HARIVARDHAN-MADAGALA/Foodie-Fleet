package com.fooddelivery.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderTrackingController {

    @MessageMapping("/track")
    @SendTo("/topic/orders")
    public String trackOrder(String orderId) {
        return "Order " + orderId + " is being tracked";
    }
}
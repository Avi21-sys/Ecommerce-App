package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service){
        this.service = service;
    }

    /**
     * Place a new order
     */
    @PostMapping
    public OrderDto placeOrder(@RequestBody OrderDto dto, @RequestHeader("X-User-Id") Long userId){
        return service.placeOrder(dto, userId);
    }

    /**
     * Get all orders for a user
     */
    @GetMapping
    public List<OrderDto> getOrders(@RequestHeader("X-User-Id") Long userId){
        return service.getOrders(userId);
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId, @RequestHeader("X-User-Id") Long userId){
        return service.getOrderById(orderId, userId);
    }

    /**
     * Cancel an order
     */
    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Long orderId, @RequestHeader("X-User-Id") Long userId){
        service.cancelOrder(orderId, userId);
    }
}


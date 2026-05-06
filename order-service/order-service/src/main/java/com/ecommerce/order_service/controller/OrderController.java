package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService service;

    public OrderController(OrderService service){
        this.service = service;
    }

    /**
     * Place a new order
     */
    @PostMapping
    public OrderDto placeOrder(@Valid @RequestBody OrderDto dto, @RequestHeader("X-User-Id") Long userId){
        logger.info("Placing new order for user: {} with total amount: {}", userId, dto.getTotalAmount());
        try {
            OrderDto result = service.placeOrder(dto, userId);
            logger.info("Order placed successfully. Order ID: {}, User ID: {}", result.getId(), userId);
            return result;
        } catch (Exception e) {
            logger.error("Error placing order for user: {}", userId, e);
            throw e;
        }
    }

    /**
     * Get all orders for a user
     */
    @GetMapping
    public List<OrderDto> getOrders(@RequestHeader("X-User-Id") Long userId){
        logger.info("Fetching all orders for user: {}", userId);
        try {
            List<OrderDto> orders = service.getOrders(userId);
            logger.info("Found {} order(s) for user: {}", orders.size(), userId);
            return orders;
        } catch (Exception e) {
            logger.error("Error fetching orders for user: {}", userId, e);
            throw e;
        }
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId, @RequestHeader("X-User-Id") Long userId){
        logger.info("Fetching order with ID: {} for user: {}", orderId, userId);
        try {
            OrderDto order = service.getOrderById(orderId, userId);
            logger.info("Order found. Order ID: {}, Total Amount: {}", orderId, order.getTotalAmount());
            return order;
        } catch (Exception e) {
            logger.error("Error fetching order {} for user: {}", orderId, userId, e);
            throw e;
        }
    }

    /**
     * Cancel an order
     */
    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Long orderId, @RequestHeader("X-User-Id") Long userId){
        logger.info("Cancelling order with ID: {} for user: {}", orderId, userId);
        try {
            service.cancelOrder(orderId, userId);
            logger.info("Order cancelled successfully. Order ID: {}", orderId);
        } catch (Exception e) {
            logger.error("Error cancelling order {} for user: {}", orderId, userId, e);
            throw e;
        }
    }
}

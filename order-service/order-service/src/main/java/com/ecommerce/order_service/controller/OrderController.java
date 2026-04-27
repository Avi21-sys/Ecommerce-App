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

    @PostMapping
    public OrderDto placeOrder(@RequestBody OrderDto dto, @RequestHeader("X-User-Id") Long userId){
        return service.placeOrder(dto, userId);
    }

    @GetMapping
    public List<OrderDto> getOrders(@RequestHeader("X-User-Id") Long userId){
        return service.getOrders(userId);
    }
}

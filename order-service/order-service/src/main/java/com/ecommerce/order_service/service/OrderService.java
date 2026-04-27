package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.dto.OrderItemDto;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.repository.OrderItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;

    public OrderService(OrderRepository orderRepo, OrderItemRepository itemRepo){
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
    }

    private OrderDto toDto(Order order) {

        List<OrderItemDto> items = itemRepo
                .findByOrderId(order.getId())
                .stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderDto(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getUserId(),
                items
        );
    }

    public OrderDto placeOrder(OrderDto dto, Long userId){

        Order order = new Order(
                null,
                dto.getTotalAmount(),
                "PLACED",
                userId
        );

        Order savedOrder = orderRepo.save(order);

        dto.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem(
                    null,
                    item.getProductId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    savedOrder.getId()
            );
            itemRepo.save(orderItem);
        });

        return toDto(savedOrder);
    }

    public List<OrderDto> getOrders(Long userId){
        return orderRepo.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }
}

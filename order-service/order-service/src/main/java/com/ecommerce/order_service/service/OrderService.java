package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.repository.OrderItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;

    public OrderService(OrderRepository orderRepo, OrderItemRepository itemRepo){
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
    }

    public OrderDto placeOrder(OrderDto dto){
        // save order
        Order order = new Order(null, dto.getTotalAmount(), "PLACED", 1L);
        Order saveOrder = orderRepo.save(order);

        // save item
        dto.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem(
                    null,
                    item.getProductId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    saveOrder.getId()
            );
            itemRepo.save(orderItem);
        });
        return dto;
    }
}

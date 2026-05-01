package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.dto.OrderItemDto;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.exception.PaymentFailedException;
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
        
        // Validate input
        if (dto == null) {
            throw new IllegalArgumentException("Order DTO cannot be null");
        }
        
        if (dto.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Validate each item
        for (OrderItemDto item : dto.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than zero");
            }
            if (item.getPrice() <= 0) {
                throw new IllegalArgumentException("Item price must be greater than zero");
            }
        }

        // Simulate payment processing
        if (!processPayment(userId, dto.getTotalAmount())) {
            throw new PaymentFailedException("Payment processing failed. Please check your payment information and try again.");
        }

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

    /**
     * Simulate payment processing
     */
    private boolean processPayment(Long userId, double amount) {
        // Placeholder for actual payment processing
        // For example, call payment gateway API
        // Return false if payment fails
        
        if (userId == null || userId <= 0) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        // Simulating successful payment
        // In production, integrate with payment gateway
        return true;
    }

    public List<OrderDto> getOrders(Long userId){
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }
        
        List<Order> orders = orderRepo.findByUserId(userId);
        
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found for user: " + userId);
        }
        
        return orders.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Retrieve a single order by ID
     */
    public OrderDto getOrderById(Long orderId, Long userId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // Verify the order belongs to the user
        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException("Order not found for user: " + userId);
        }

        return toDto(order);
    }

    /**
     * Cancel an order
     */
    public void cancelOrder(Long orderId, Long userId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // Verify the order belongs to the user
        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException("Order not found for user: " + userId);
        }

        // Only allow cancellation if order is in PLACED or PENDING status
        if (!"PLACED".equals(order.getStatus()) && !"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order cannot be cancelled with status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        orderRepo.save(order);
    }
}

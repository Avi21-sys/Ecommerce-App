package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.dto.OrderItemDto;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.exception.PaymentFailedException;
import com.ecommerce.order_service.repository.OrderItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
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
        logger.debug("Starting placeOrder for user: {} with DTO: {}", userId, dto);
        
        // Validate input
        if (dto == null) {
            logger.warn("placeOrder called with null OrderDto for user: {}", userId);
            throw new IllegalArgumentException("Order DTO cannot be null");
        }
        
        if (dto.getTotalAmount() <= 0) {
            logger.warn("placeOrder called with invalid total amount: {}", dto.getTotalAmount());
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            logger.warn("placeOrder called with no items for user: {}", userId);
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Validate each item
        for (OrderItemDto item : dto.getItems()) {
            if (item.getProductId() == null) {
                logger.warn("placeOrder called with null product ID");
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (item.getQuantity() <= 0) {
                logger.warn("placeOrder called with invalid quantity: {} for product: {}", item.getQuantity(), item.getProductId());
                throw new IllegalArgumentException("Item quantity must be greater than zero");
            }
            if (item.getPrice() <= 0) {
                logger.warn("placeOrder called with invalid price: {} for product: {}", item.getPrice(), item.getProductId());
                throw new IllegalArgumentException("Item price must be greater than zero");
            }
        }

        logger.info("Validating order for user: {} - all validations passed", userId);

        // Simulate payment processing
        logger.info("Processing payment for user: {} with amount: {}", userId, dto.getTotalAmount());
        if (!processPayment(userId, dto.getTotalAmount())) {
            logger.error("Payment processing failed for user: {} with amount: {}", userId, dto.getTotalAmount());
            throw new PaymentFailedException("Payment processing failed. Please check your payment information and try again.");
        }
        logger.info("Payment successful for user: {}", userId);

        Order order = new Order(
                null,
                dto.getTotalAmount(),
                "PLACED",
                userId
        );

        Order savedOrder = orderRepo.save(order);
        logger.info("Order saved successfully with ID: {} for user: {}", savedOrder.getId(), userId);

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
            logger.debug("Order item saved - Product: {}, Quantity: {}", item.getProductId(), item.getQuantity());
        });

        logger.info("Order placement completed. Order ID: {}, Total Amount: {}, Item Count: {}", 
            savedOrder.getId(), dto.getTotalAmount(), dto.getItems().size());
        return toDto(savedOrder);
    }

    /**
     * Simulate payment processing
     */
    private boolean processPayment(Long userId, double amount) {
        logger.debug("Processing payment - User ID: {}, Amount: {}", userId, amount);
        // Placeholder for actual payment processing
        // For example, call payment gateway API
        // Return false if payment fails
        
        if (userId == null || userId <= 0) {
            logger.error("Invalid user ID for payment: {}", userId);
            return false;
        }
        
        if (amount <= 0) {
            logger.error("Invalid amount for payment: {}", amount);
            return false;
        }
        
        // Simulating successful payment
        // In production, integrate with payment gateway
        logger.debug("Payment simulation successful");
        return true;
    }

    public List<OrderDto> getOrders(Long userId){
        logger.info("Fetching all orders for user: {}", userId);
        
        if (userId == null || userId <= 0) {
            logger.warn("getOrders called with invalid user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be valid");
        }
        
        List<Order> orders = orderRepo.findByUserId(userId);
        logger.debug("Found {} orders for user: {}", orders.size(), userId);
        
        if (orders.isEmpty()) {
            logger.warn("No orders found for user: {}", userId);
            throw new OrderNotFoundException("No orders found for user: " + userId);
        }
        
        logger.info("Successfully fetched {} orders for user: {}", orders.size(), userId);
        return orders.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Retrieve a single order by ID
     */
    public OrderDto getOrderById(Long orderId, Long userId) {
        logger.info("Fetching order - Order ID: {}, User ID: {}", orderId, userId);
        
        if (orderId == null || orderId <= 0) {
            logger.warn("getOrderById called with invalid order ID: {}", orderId);
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (userId == null || userId <= 0) {
            logger.warn("getOrderById called with invalid user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be valid");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found - Order ID: {}", orderId);
                    return new OrderNotFoundException("Order not found with ID: " + orderId);
                });

        // Verify the order belongs to the user
        if (!order.getUserId().equals(userId)) {
            logger.warn("Access denied - Order {} does not belong to user: {}", orderId, userId);
            throw new OrderNotFoundException("Order not found for user: " + userId);
        }

        logger.info("Order found and verified - Order ID: {}, User ID: {}", orderId, userId);
        return toDto(order);
    }

    /**
     * Cancel an order
     */
    public void cancelOrder(Long orderId, Long userId) {
        logger.info("Cancelling order - Order ID: {}, User ID: {}", orderId, userId);
        
        if (orderId == null || orderId <= 0) {
            logger.warn("cancelOrder called with invalid order ID: {}", orderId);
            throw new IllegalArgumentException("Order ID must be valid");
        }
        
        if (userId == null || userId <= 0) {
            logger.warn("cancelOrder called with invalid user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be valid");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found for cancellation - Order ID: {}", orderId);
                    return new OrderNotFoundException("Order not found with ID: " + orderId);
                });

        // Verify the order belongs to the user
        if (!order.getUserId().equals(userId)) {
            logger.warn("Access denied - Order {} does not belong to user: {}", orderId, userId);
            throw new OrderNotFoundException("Order not found for user: " + userId);
        }

        // Only allow cancellation if order is in PLACED or PENDING status
        if (!"PLACED".equals(order.getStatus()) && !"PENDING".equals(order.getStatus())) {
            logger.warn("Cannot cancel order {} with status: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("Order cannot be cancelled with status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        orderRepo.save(order);
        logger.info("Order cancelled successfully - Order ID: {}, Previous Status: PLACED/PENDING", orderId);
    }
}

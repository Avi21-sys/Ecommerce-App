package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService service;

    public CartController(CartService service){
        this.service = service;
    }

    @GetMapping
    public List<CartItemDto> getCart(@RequestHeader("X-User-Id") Long userId){
        logger.info("GET request: Fetching cart for user with ID: {}", userId);
        List<CartItemDto> cartItems = service.getCart(userId);
        logger.debug("Retrieved {} items from cart for user: {}", cartItems.size(), userId);
        return cartItems;
    }

    @PostMapping
    public CartItemDto add(@RequestBody CartItemDto dto, @RequestHeader("X-User-Id") Long userId){
        logger.info("POST request: Adding product {} to cart for user {}", dto.getProductId(), userId);
        logger.debug("Product details - Name: {}, Price: {}, Quantity: {}", 
            dto.getProductName(), dto.getPrice(), dto.getQuantity());
        CartItemDto result = service.addToCart(dto, userId);
        logger.info("Successfully added product to cart. Cart item ID: {}", result.getId());
        return result;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId){
        logger.info("DELETE request: Removing cart item {} for user {}", id, userId);
        service.remove(id, userId);
        logger.info("Successfully removed cart item {} for user {}", id, userId);
    }
}

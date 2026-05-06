package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.entity.CartItem;
import com.ecommerce.cart_service.exception.CartNotFoundException;
import com.ecommerce.cart_service.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final CartRepository repo;

    public CartService(CartRepository repo){
        this.repo=repo;
    }

    // Entity --> DTO
    public CartItemDto toDto(CartItem item){
        return  new CartItemDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity()
        );
    }

    // DTO --> Entity
    public CartItem toEntity(CartItemDto dto, Long userId){
        return new CartItem(
                null,
                dto.getProductId(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getQuantity(),
                userId
        );
    }

    // GET Cart
    public List<CartItemDto> getCart(Long userId){
        logger.debug("Fetching cart items for user: {}", userId);
        List<CartItemDto> cartItems = repo.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
        logger.info("Cart fetch successful. Found {} items for user: {}", cartItems.size(), userId);
        return cartItems;
    }

    public CartItemDto addToCart(CartItemDto dto, Long userId){
        logger.debug("Adding to cart - ProductId: {}, UserId: {}, Quantity: {}", 
            dto.getProductId(), userId, dto.getQuantity());

        CartItem existing = repo.findByProductIdAndUserId(dto.getProductId(), userId);

        if(existing != null){
            logger.debug("Product {} already exists in cart for user {}. Updating quantity from {} to {}", 
                dto.getProductId(), userId, existing.getQuantity(), 
                existing.getQuantity() + dto.getQuantity());
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            CartItemDto result = toDto(repo.save(existing));
            logger.info("Successfully updated cart item {} for user {}", existing.getId(), userId);
            return result;
        }

        logger.debug("Adding new product {} to cart for user {}", dto.getProductId(), userId);
        CartItem saved = repo.save(toEntity(dto, userId));
        CartItemDto result = toDto(saved);
        logger.info("Successfully added new item to cart. CartItemId: {}, ProductId: {}, UserId: {}", 
            result.getId(), dto.getProductId(), userId);
        return result;
    }

    public void remove(Long id, Long userId){
        logger.debug("Attempting to remove cart item {} for user {}", id, userId);
        
        CartItem item = repo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cart item not found - CartItemId: {}, UserId: {}", id, userId);
                    return new CartNotFoundException("Cart item with id " + id + " not found");
                });

        if (!item.getUserId().equals(userId)) {
            logger.warn("Unauthorized delete attempt - CartItemId: {}, RequestUserId: {}, ItemUserId: {}", 
                id, userId, item.getUserId());
            throw new CartNotFoundException("Unauthorized: Cart item does not belong to user " + userId);
        }

        repo.deleteById(id);
        logger.info("Successfully removed cart item {} for user {}", id, userId);
    }

}

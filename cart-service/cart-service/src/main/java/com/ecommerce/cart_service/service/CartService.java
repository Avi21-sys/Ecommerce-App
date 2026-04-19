package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.entity.CartItem;
import com.ecommerce.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

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
        return repo.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CartItemDto addToCart(CartItemDto dto, Long userId){

        CartItem existing = repo.findByProductIdAndUserId(dto.getProductId(), userId);

        if(existing != null){
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            return toDto(repo.save(existing));
        }

        CartItem saved = repo.save(toEntity(dto, userId));
        return toDto(saved);
    }

    public void remove(Long id, Long userId){
        CartItem item = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        repo.deleteById(id);
    }

}

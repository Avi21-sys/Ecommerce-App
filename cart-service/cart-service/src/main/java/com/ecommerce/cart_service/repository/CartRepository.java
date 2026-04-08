package com.ecommerce.cart_service.repository;

import com.ecommerce.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);
    CartItem findByProductIdAndUserId(Long productId, Long userId);
}

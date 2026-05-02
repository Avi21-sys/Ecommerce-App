package com.ecommerce.cart_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "cart_item",
    indexes = {
        @Index(name = "idx_cart_item_user_id", columnList = "userId"),
        @Index(name = "idx_cart_item_product_user", columnList = "productId, userId")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_item_product_user", columnNames = {"productId", "userId"})
    }
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long userId;
}

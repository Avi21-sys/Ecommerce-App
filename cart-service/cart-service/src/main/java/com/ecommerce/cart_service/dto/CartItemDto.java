package com.ecommerce.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}

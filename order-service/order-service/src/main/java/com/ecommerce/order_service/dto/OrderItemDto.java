package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @Positive(message = "Price must be positive")
    private double price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Product price must be positive")
    private double price;

    private String image;

    @NotBlank(message = "Product category is required")
    private String category;
}

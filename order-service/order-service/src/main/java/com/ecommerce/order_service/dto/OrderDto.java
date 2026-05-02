package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;

    @Positive(message = "Total amount must be positive")
    private double totalAmount;

    private String status;

    private Long userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;
}

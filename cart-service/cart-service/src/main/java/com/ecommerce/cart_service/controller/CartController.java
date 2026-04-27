package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service){
        this.service = service;
    }

    @GetMapping
    public List<CartItemDto> getCart(@RequestHeader("X-User-Id") Long userId){
        return service.getCart(userId);
    }

    @PostMapping
    public CartItemDto add(@RequestBody CartItemDto dto, @RequestHeader("X-User-Id") Long userId){
        return service.addToCart(dto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId){
        service.remove(id, userId);
    }
}

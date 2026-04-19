package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
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
    public List<CartItemDto> getCart(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return service.getCart(userId);
    }

    @PostMapping
    public CartItemDto add(@RequestBody CartItemDto dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return service.addToCart(dto, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        service.remove(id, userId);
    }
}

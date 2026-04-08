package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CartItemDto;
import com.ecommerce.cart_service.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService service;

    public CartController(CartService service){
        this.service = service;
    }

    private final Long USER_ID = 1L; // temporary (no auth yet)

    @GetMapping
    public List<CartItemDto> getCart(){
        return service.getCart(USER_ID);
    }

    @PostMapping
    public CartItemDto add(@RequestBody CartItemDto dto){
        return service.addToCart(dto, USER_ID);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.remove(id);
    }
}

package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req){
        return service.login(req.getUsername(), req.getPassword());
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody LoginRequest req){
        return service.register(req.getUsername(), req.getPassword());
    }
}

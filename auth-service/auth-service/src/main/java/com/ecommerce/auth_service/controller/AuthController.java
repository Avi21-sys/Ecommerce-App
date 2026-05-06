package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req){
        logger.info("Login attempt for username: {}", req.getUsername());
        LoginResponse response = service.login(req.getUsername(), req.getPassword());
        logger.info("Login successful for username: {}", req.getUsername());
        return response;
    }

    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody LoginRequest req){
        logger.info("Registration attempt for username: {}", req.getUsername());
        LoginResponse response = service.register(req.getUsername(), req.getPassword());
        logger.info("Registration successful for username: {}", req.getUsername());
        return response;
    }
}

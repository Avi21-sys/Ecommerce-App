package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder encoder){
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    public LoginResponse login(String username, String password){
        User user = repo.findByUsername(username);

        if (user != null && encoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            return new LoginResponse(token, username);
        }

        throw new RuntimeException("Invalid Credentials");
    }

    public LoginResponse register(String username, String password){
        User existing = repo.findByUsername(username);

        if (existing != null) {
            throw new RuntimeException("Username already exists");
        }

        String encodedPassword = encoder.encode(password);

        User saved = repo.save(new User(null, username, encodedPassword));

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername());

        return new LoginResponse(token, saved.getUsername());
    }
}
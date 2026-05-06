package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.exception.InvalidCredentialsException;
import com.ecommerce.auth_service.exception.UsernameAlreadyExistsException;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository repo, JwtUtil jwtUtil, BCryptPasswordEncoder encoder){
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    public LoginResponse login(String username, String password){
        logger.debug("Attempting to authenticate user: {}", username);
        User user = repo.findByUsername(username);

        if (user != null && encoder.matches(password, user.getPassword())) {
            logger.debug("User found and password matched for username: {}", username);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            logger.info("JWT token generated successfully for username: {}", username);
            return new LoginResponse(token, username);
        }

        logger.warn("Authentication failed for username: {} - Invalid credentials", username);
        throw new InvalidCredentialsException("Invalid Credentials");
    }

    public LoginResponse register(String username, String password){
        logger.debug("Attempting to register user: {}", username);
        User existing = repo.findByUsername(username);

        if (existing != null) {
            logger.warn("Registration failed for username: {} - Username already exists", username);
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        logger.debug("Username {} is available, proceeding with registration", username);
        String encodedPassword = encoder.encode(password);

        User saved = repo.save(new User(null, username, encodedPassword));
        logger.debug("User saved to database with ID: {}", saved.getId());

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername());
        logger.info("New user registered successfully with username: {}", username);

        return new LoginResponse(token, saved.getUsername());
    }
}
package com.ecommerce.order_service.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException{
        HttpServletRequest req = (HttpServletRequest) request;

        String header = req.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")){
            logger.debug("No valid Authorization header found for request: {}", req.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        logger.debug("JWT token extracted from Authorization header for request: {}", req.getRequestURI());

        try{
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                logger.warn("JWT token does not contain userId for request: {}", req.getRequestURI());
                throw new IllegalArgumentException("JWT does not contain userId");
            }

            // store in request
            req.setAttribute("username", username);
            req.setAttribute("userId", userId);
            logger.info("Successfully authenticated user: {} with ID: {} for request: {}", username, userId, req.getRequestURI());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.error("JWT validation failed - Error: {}, Request: {}", e.getMessage(), req.getRequestURI(), e);
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}

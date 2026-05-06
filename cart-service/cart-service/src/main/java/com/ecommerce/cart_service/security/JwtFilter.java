package com.ecommerce.cart_service.security;

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
        String requestUri = req.getRequestURI();

        String header = req.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")){
            logger.debug("No valid Authorization header found for request: {}", requestUri);
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        logger.debug("Processing JWT token for request: {}", requestUri);

        try{
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                logger.warn("JWT token does not contain userId for request: {}", requestUri);
                throw new IllegalArgumentException("JWT does not contain userId");
            }

            logger.info("JWT token validated successfully - Username: {}, UserId: {}, Request: {}", 
                username, userId, requestUri);
            
            req.setAttribute("username", username);
            req.setAttribute("userId", userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.warn("JWT validation failed - Exception: {}, Message: {}, Request: {}", 
                e.getClass().getName(), e.getMessage(), requestUri);
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}

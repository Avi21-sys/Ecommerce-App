package com.ecommerce.cart_service.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter implements Filter {

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
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try{
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                throw new IllegalArgumentException("JWT does not contain userId");
            }

            req.setAttribute("username", username);
            req.setAttribute("userId", userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}

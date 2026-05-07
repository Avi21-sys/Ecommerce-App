package com.ecommerce.api_gateway.filter;

import com.ecommerce.api_gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (HttpMethod.OPTIONS.equals(method)) {
            log.debug("OPTIONS request - skipping auth filter for path={}", path);
            return chain.filter(exchange);
        }

        if (path.startsWith("/api/auth/")) {
            log.debug("Auth path - skipping auth filter for path={}", path);
            return chain.filter(exchange);
        }

        if (HttpMethod.GET.equals(method) && path.startsWith("/api/products/")) {
            log.debug("Public products GET - skipping auth for path={}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Missing or invalid Authorization header for request to {}", path);
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.validateToken(token)) {
                log.debug("JWT validation failed for request to {}", path);
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract userId from token and add to header
            String userId = jwtUtil.extractUserId(token);
            if (userId == null || userId.isBlank()) {
                log.debug("JWT does not contain userId claim for request to {}", path);
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            if (isAdminProtectedPath(path, method) && !"ADMIN".equalsIgnoreCase(role)) {
                log.warn("Forbidden admin access attempt to {} by userId={} role={}", path, userId, role);
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Add userId to request header for downstream services
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Id", userId)
                            .header("X-Username", username == null ? "" : username)
                            .header("X-Role", role == null ? "" : role))
                    .build();

            log.info("Authenticated request to {} for userId={} username={} role={}", path, userId, username, role);

            return chain.filter(modifiedExchange);
        } catch (Exception e) {
            log.error("Error processing JWT for request to {}: {}", path, e.getMessage(), e);
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isAdminProtectedPath(String path, HttpMethod method) {
        if (path.startsWith("/api/orders/admin/")) {
            return true;
        }

        return path.startsWith("/api/products")
                && method != null
                && !HttpMethod.GET.equals(method)
                && !HttpMethod.OPTIONS.equals(method);
    }
}

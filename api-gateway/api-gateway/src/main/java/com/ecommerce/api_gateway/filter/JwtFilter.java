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
            return chain.filter(exchange);
        }

        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        if (HttpMethod.GET.equals(method) && path.startsWith("/api/products/")) {
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

            // Add userId to request header for downstream services
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Id", userId)
                            .header("X-Username", username))
                    .build();

            return chain.filter(modifiedExchange);
        } catch (Exception e) {
            log.error("Error processing JWT for request to {}: {}", path, e.getMessage(), e);
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

}

package com.ecommerce.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
// Note: this class is intentionally not a Spring bean to avoid duplicating CORS headers.
// If you need to enable programmatic CORS handling, re-add the @Component annotation.
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CorsFilter implements GlobalFilter, Ordered {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String origin = exchange.getRequest().getHeaders().getOrigin();

        if (!ALLOWED_ORIGIN.equals(origin)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            headers.set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
            headers.set("Access-Control-Allow-Credentials", "true");
            headers.set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
            headers.set("Access-Control-Allow-Headers", "*");
            headers.set("Access-Control-Expose-Headers", "Authorization, Content-Type, X-User-Id, X-Username");
            headers.set("Vary", "Origin");
            return Mono.empty();
        });

        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

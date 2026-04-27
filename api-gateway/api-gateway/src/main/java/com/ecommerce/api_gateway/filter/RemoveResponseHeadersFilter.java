package com.ecommerce.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filter to remove CORS headers from backend service responses to prevent duplication.
 * The gateway's CorsWebFilter handles CORS, so we strip it from backend responses.
 */
@Component
public class RemoveResponseHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Remove CORS headers from backend responses to avoid duplication
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Origin");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Credentials");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Methods");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Headers");
            exchange.getResponse().getHeaders().remove("Access-Control-Expose-Headers");
            exchange.getResponse().getHeaders().remove("Access-Control-Max-Age");
        }));
    }

    @Override
    public int getOrder() {
        // Run after other filters to remove backend CORS headers
        return 100;
    }
}


package com.ecommerce.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RemoveResponseHeadersFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Remove CORS headers from backend responses to avoid duplication
            String path = exchange.getRequest().getURI().getPath();
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Origin");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Credentials");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Methods");
            exchange.getResponse().getHeaders().remove("Access-Control-Allow-Headers");
            exchange.getResponse().getHeaders().remove("Access-Control-Expose-Headers");
            exchange.getResponse().getHeaders().remove("Access-Control-Max-Age");
            log.debug("Stripped backend CORS headers for path={}", path);
        }));
    }

    @Override
    public int getOrder() {
        // Run after other filters to remove backend CORS headers
        return 100;
    }
}


package com.ezra.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/v1/products/**")
                        .uri("lb://product-service"))
                .route("customer-service", r -> r
                        .path("/api/v1/customers/**")
                        .uri("lb://customer-service"))
                .route("loan-service", r -> r
                        .path("/api/v1/loans/**")
                        .uri("lb://loan-service"))
                .route("notification-service", r -> r
                        .path("/api/v1/notification/**", "/api/v1/templates/**")
                        .uri("lb://notification-service"))
                .build();
    }
}

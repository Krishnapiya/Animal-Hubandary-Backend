package com.keltron.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator keltronGatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("admin-service-legacy",
                        r -> r.path("/admin/**").uri("lb://admin-service"))
                .route("admin-service-rbac-admin",
                        r -> r.path("/api/admin/**").uri("lb://admin-service"))
                .route("admin-service-rbac-me",
                        r -> r.path("/api/me/**").uri("lb://admin-service"))
                .route("springSecurity",
                        r -> r.path("/auth/**").uri("lb://springSecurity"))
                .build();
    }
}

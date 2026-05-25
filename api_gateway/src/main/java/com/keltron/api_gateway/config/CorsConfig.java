package com.keltron.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for API Gateway
 * This configuration allows cross-origin requests from the frontend application.
 * 
 * For Production:
 * - Replace setAllowedOriginPatterns with setAllowedOrigins and specify exact origins
 * - Example: corsConfig.setAllowedOrigins(Arrays.asList("https://yourdomain.com", "https://www.yourdomain.com"));
 * - Remove or restrict allowed headers based on your requirements
 * 
 * @author KELTRON - IT Business Group
 */
@Configuration
public class CorsConfig {

    private static final List<String> DEFAULT_EXPOSED_HEADERS = Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Total-Count",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials"
    );

    private final CorsConfigProperties corsConfigProperties;

    public CorsConfig(CorsConfigProperties corsConfigProperties) {
        this.corsConfigProperties = corsConfigProperties;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        List<String> allowedOrigins = corsConfigProperties.getAllowedOrigins();
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            if (allowedOrigins.contains("*")) {
                corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
            } else {
                corsConfig.setAllowedOrigins(allowedOrigins);
            }
        } else {
            corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        }

        List<String> allowedMethods = corsConfigProperties.getAllowedMethods();
        corsConfig.setAllowedMethods(
            allowedMethods != null && !allowedMethods.isEmpty()
                ? allowedMethods
                : Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
        );

        List<String> allowedHeaders = corsConfigProperties.getAllowedHeaders();
        corsConfig.setAllowedHeaders(
            allowedHeaders != null && !allowedHeaders.isEmpty() ? allowedHeaders : Arrays.asList("*")
        );

        corsConfig.setAllowCredentials(
            corsConfigProperties.getAllowCredentials() != null ? corsConfigProperties.getAllowCredentials() : true
        );

        List<String> exposedHeaders = corsConfigProperties.getExposedHeaders();
        corsConfig.setExposedHeaders(
            exposedHeaders != null && !exposedHeaders.isEmpty() ? exposedHeaders : DEFAULT_EXPOSED_HEADERS
        );

        corsConfig.setMaxAge(corsConfigProperties.getMaxAge() != null ? corsConfigProperties.getMaxAge() : 3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}


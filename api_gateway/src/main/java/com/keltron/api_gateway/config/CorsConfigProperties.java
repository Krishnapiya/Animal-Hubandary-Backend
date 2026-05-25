package com.keltron.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * CORS Configuration Properties
 * Allows CORS settings to be configured via application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsConfigProperties {
    
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private Boolean allowCredentials = true;
    private Long maxAge = 3600L;
    
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
    
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
    
    public List<String> getAllowedMethods() {
        return allowedMethods;
    }
    
    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }
    
    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }
    
    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }
    
    public List<String> getExposedHeaders() {
        return exposedHeaders;
    }
    
    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }
    
    public Boolean getAllowCredentials() {
        return allowCredentials;
    }
    
    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
    
    public Long getMaxAge() {
        return maxAge;
    }
    
    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }
}


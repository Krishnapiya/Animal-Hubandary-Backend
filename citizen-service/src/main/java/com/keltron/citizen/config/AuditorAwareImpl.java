package com.keltron.citizen.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("DEBUG: No authentication or not authenticated");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        System.out.println("DEBUG: Principal = " + principal);

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            System.out.println("DEBUG: Username (UserDetails) = " + username);
            return Optional.of(username);
        } else if (principal instanceof String) {
            System.out.println("DEBUG: Username (String) = " + principal);
            return Optional.of((String) principal);
        } else if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) principal;
            
         
            String username = jwt.getSubject();  
            
            System.out.println("DEBUG: Username (Jwt sub) = " + username);
            return Optional.ofNullable(username);
        } else {
            System.out.println("DEBUG: Unknown principal type = " + principal.getClass());
            return Optional.empty();
        }
    }
}


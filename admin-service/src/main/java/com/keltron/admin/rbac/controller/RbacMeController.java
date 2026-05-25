package com.keltron.admin.rbac.controller;

import com.keltron.admin.rbac.service.RbacService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.responses.AbstractResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
public class RbacMeController {

    private final RbacService rbacService;

    public RbacMeController(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @GetMapping("/navigation")
    public ResponseEntity<AbstractResponse> navigation(Authentication authentication) {
        List<String> roleNames = extractRoles(authentication);
        return new ResponseBuilder().withData(rbacService.getNavigationForCurrentUser(roleNames)).build();
    }

    @GetMapping("/permissions")
    public ResponseEntity<AbstractResponse> permissions(Authentication authentication) {
        List<String> roleNames = extractRoles(authentication);
        return new ResponseBuilder().withData(rbacService.getPermissionsForCurrentUser(roleNames)).build();
    }

    private List<String> extractRoles(Authentication authentication) {
        List<String> authorityRoles = authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring("ROLE_".length()))
            .distinct()
            .collect(Collectors.toList());
        if (!authorityRoles.isEmpty()) {
            return authorityRoles;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            List<String> claimRoles = jwt.getClaimAsStringList("roles");
            if (claimRoles != null && !claimRoles.isEmpty()) {
                return claimRoles;
            }
            String claimRole = jwt.getClaimAsString("role");
            if (claimRole != null && !claimRole.isBlank()) {
                return List.of(claimRole);
            }
        }
        return List.of();
    }
}

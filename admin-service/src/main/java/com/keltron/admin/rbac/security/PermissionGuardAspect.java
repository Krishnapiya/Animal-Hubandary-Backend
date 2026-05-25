package com.keltron.admin.rbac.security;

import com.keltron.admin.rbac.service.RbacService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class PermissionGuardAspect {

    private final RbacService rbacService;

    public PermissionGuardAspect(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @Around("@annotation(requirePermission)")
    public Object validatePermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roleNames = extractRoles(authentication);
        if (roleNames.stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role))) {
            return joinPoint.proceed();
        }

        if (!rbacService.hasPermissionByRoleNames(roleNames, requirePermission.menu(), requirePermission.action())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied");
        }
        return joinPoint.proceed();
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

package com.keltron.admin.rbac.controller;

import com.keltron.admin.rbac.dto.MenuRequest;
import com.keltron.admin.rbac.dto.ModuleRequest;
import com.keltron.admin.rbac.dto.PermissionUpdateRequest;
import com.keltron.admin.rbac.service.RbacService;
import com.keltron.utility.ResponseBuilder;
import com.keltron.utility.responses.AbstractResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class RbacAdminController {

    private final RbacService rbacService;

    public RbacAdminController(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @GetMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> listModules() {
        return new ResponseBuilder().withData(rbacService.listModules()).build();
    }

    @PostMapping("/modules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> createModule(@RequestBody ModuleRequest request) {
        return new ResponseBuilder().withData(rbacService.createModule(request)).build();
    }

    @RequestMapping(value = "/modules/{id}", method = { RequestMethod.PATCH, RequestMethod.PUT })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> updateModule(
        @PathVariable Long id,
        @RequestBody ModuleRequest request
    ) {
        return new ResponseBuilder().withData(rbacService.updateModule(id, request)).build();
    }

    @GetMapping("/menus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> listMenus() {
        return new ResponseBuilder().withData(rbacService.listMenus()).build();
    }

    @PostMapping("/menus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> createMenu(@RequestBody MenuRequest request) {
        return new ResponseBuilder().withData(rbacService.createMenu(request)).build();
    }

    @RequestMapping(value = "/menus/{id}", method = { RequestMethod.PATCH, RequestMethod.PUT })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> updateMenu(
        @PathVariable Long id,
        @RequestBody MenuRequest request
    ) {
        return new ResponseBuilder().withData(rbacService.updateMenu(id, request)).build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> listRoles() {
        return new ResponseBuilder().withData(rbacService.listRoles()).build();
    }

    @GetMapping("/actions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> listActions() {
        return new ResponseBuilder().withData(rbacService.listActions()).build();
    }

    @GetMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> getRolePermissions(@PathVariable Integer roleId) {
        return new ResponseBuilder().withData(Map.of("permissions", rbacService.getPermissionsByRole(roleId))).build();
    }

    /**
     * PUT included because some clients/proxies mishandle PATCH. Body optional: omit or use {@code {"permissions":{}}}.
     */
    @RequestMapping(value = "/roles/{roleId}/permissions", method = { RequestMethod.PATCH, RequestMethod.PUT })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbstractResponse> updateRolePermissions(
        @PathVariable Integer roleId,
        @RequestBody(required = false) PermissionUpdateRequest request
    ) {
        rbacService.updatePermissionsByRole(roleId, request != null ? request : new PermissionUpdateRequest());
        return new ResponseBuilder().withData(Map.of("updated", true)).build();
    }
}

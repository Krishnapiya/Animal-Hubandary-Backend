package com.keltron.admin.rbac.service;

import com.keltron.admin.rbac.dto.MenuRequest;
import com.keltron.admin.rbac.dto.ModuleRequest;
import com.keltron.admin.rbac.dto.PermissionUpdateRequest;

import java.util.List;
import java.util.Map;

public interface RbacService {
    Object createModule(ModuleRequest request);
    Object updateModule(Long id, ModuleRequest request);
    Object createMenu(MenuRequest request);
    Object updateMenu(Long id, MenuRequest request);
    List<Map<String, Object>> listModules();
    List<Map<String, Object>> listMenus();
    List<Map<String, Object>> listRoles();
    List<Map<String, Object>> listActions();
    List<Map<String, Object>> getNavigationForCurrentUser(List<String> roleNames);
    Map<String, Map<String, Boolean>> getPermissionsForCurrentUser(List<String> roleNames);
    Map<String, Map<String, Boolean>> getPermissionsByRole(Integer roleId);
    void updatePermissionsByRole(Integer roleId, PermissionUpdateRequest request);
    boolean hasPermission(Integer roleId, String menuKey, String action);
    boolean hasPermissionByRoleNames(List<String> roleNames, String menuKey, String action);
    void evictRoleCache(Integer roleId);
}

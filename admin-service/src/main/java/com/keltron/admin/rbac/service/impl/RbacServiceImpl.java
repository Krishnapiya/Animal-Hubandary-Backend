package com.keltron.admin.rbac.service.impl;

import com.keltron.admin.rbac.dto.MenuRequest;
import com.keltron.admin.rbac.dto.ModuleRequest;
import com.keltron.admin.rbac.dto.PermissionUpdateRequest;
import com.keltron.admin.rbac.dto.MenuActionRequest;
import com.keltron.admin.rbac.entity.MenuMaster;
import com.keltron.admin.rbac.entity.MenuAction;
import com.keltron.admin.rbac.entity.ModuleMaster;
import com.keltron.admin.rbac.entity.PermissionAction;
import com.keltron.admin.rbac.entity.RoleMenuPermission;
import com.keltron.admin.rbac.repository.MenuMasterRepository;
import com.keltron.admin.rbac.repository.MenuActionRepository;
import com.keltron.admin.rbac.repository.ModuleMasterRepository;
import com.keltron.admin.rbac.repository.PermissionActionRepository;
import com.keltron.admin.rbac.repository.RoleMenuPermissionRepository;
import com.keltron.admin.rbac.service.RbacService;
import com.keltron.admin.repository.RoleMasterRepository;
import com.keltron.utility.jpa.entity.RoleMaster;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RbacServiceImpl implements RbacService {

    private final ModuleMasterRepository moduleRepository;
    private final MenuMasterRepository menuRepository;
    private final MenuActionRepository menuActionRepository;
    private final PermissionActionRepository actionRepository;
    private final RoleMenuPermissionRepository permissionRepository;
    private final RoleMasterRepository roleMasterRepository;

    private final Map<Integer, Map<String, Map<String, Boolean>>> permissionCache = new ConcurrentHashMap<>();

    public RbacServiceImpl(
        ModuleMasterRepository moduleRepository,
        MenuMasterRepository menuRepository,
        MenuActionRepository menuActionRepository,
        PermissionActionRepository actionRepository,
        RoleMenuPermissionRepository permissionRepository,
        RoleMasterRepository roleMasterRepository
    ) {
        this.moduleRepository = moduleRepository;
        this.menuRepository = menuRepository;
        this.menuActionRepository = menuActionRepository;
        this.actionRepository = actionRepository;
        this.permissionRepository = permissionRepository;
        this.roleMasterRepository = roleMasterRepository;
    }

    @Override
    @Transactional
    public Object createModule(ModuleRequest request) {
        ModuleMaster module = new ModuleMaster();
        module.setName(request.getName());
        module.setSlug(request.getSlug());
        if (request.getDisplayOrder() != null) module.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) module.setActive(request.getActive());
        moduleRepository.save(module);
        return Map.of("id", module.getId(), "name", module.getName(), "slug", module.getSlug());
    }

    @Override
    @Transactional
    public Object updateModule(Long id, ModuleRequest request) {
        ModuleMaster module = moduleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Module not found"));
        if (request.getName() != null) module.setName(request.getName());
        if (request.getSlug() != null) module.setSlug(request.getSlug());
        if (request.getDisplayOrder() != null) module.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) module.setActive(request.getActive());
        moduleRepository.save(module);
        return Map.of(
            "id", module.getId(),
            "name", module.getName(),
            "slug", module.getSlug(),
            "displayOrder", module.getDisplayOrder(),
            "active", module.getActive()
        );
    }

    @Override
    @Transactional
    public Object createMenu(MenuRequest request) {
        ModuleMaster module = moduleRepository.findById(request.getModuleId())
            .orElseThrow(() -> new EntityNotFoundException("Module not found"));
        MenuMaster menu = new MenuMaster();
        menu.setModule(module);
        if (request.getParentId() != null) {
            MenuMaster parent = menuRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent menu not found"));
            menu.setParent(parent);
        }
        menu.setName(request.getName());
        menu.setSlug(request.getSlug());
        menu.setPath(request.getPath());
        if (request.getDisplayOrder() != null) menu.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) menu.setActive(request.getActive());
        menuRepository.save(menu);
        if (request.getPageActions() != null) {
            syncMenuPageActions(menu, request.getPageActions());
        }
        permissionCache.clear();
        return menuToAdminMap(menu);
    }

    @Override
    @Transactional
    public Object updateMenu(Long id, MenuRequest request) {
        MenuMaster menu = menuRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Menu not found"));

        if (request.getModuleId() != null) {
            ModuleMaster module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));
            menu.setModule(module);
        }

        // parentId: null means clear parent (top-level menu)
        if (request.getParentId() != null) {
            MenuMaster parent = menuRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent menu not found"));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        if (request.getName() != null) menu.setName(request.getName());
        if (request.getSlug() != null) menu.setSlug(request.getSlug());
        if (request.getPath() != null) menu.setPath(request.getPath());

        if (request.getDisplayOrder() != null) menu.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) menu.setActive(request.getActive());

        menuRepository.save(menu);
        if (request.getPageActions() != null) {
            syncMenuPageActions(menu, request.getPageActions());
        }
        permissionCache.clear();
        return menuToAdminMap(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listModules() {
        return moduleRepository.findAll().stream()
            .map(m -> Map.<String, Object>of("id", m.getId(), "name", m.getName(), "slug", m.getSlug()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMenus() {
        List<MenuMaster> all = menuRepository.findAll();
        Map<Long, List<MenuAction>> actionsByMenu = new HashMap<>();
        for (MenuAction ma : menuActionRepository.findAllWithMenuOrdered()) {
            actionsByMenu
                .computeIfAbsent(ma.getMenu().getId(), k -> new ArrayList<>())
                .add(ma);
        }
        actionsByMenu.values().forEach(list ->
            list.sort(Comparator.comparing(MenuAction::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)))
        );
        return all.stream()
            .map(m -> menuToAdminMap(m, actionsByMenu.getOrDefault(m.getId(), List.of())))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listRoles() {
        return roleMasterRepository.findAll().stream()
            .map(r -> Map.<String, Object>of("id", r.getId(), "name", r.getRoleName(), "roleName", r.getRoleName()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listActions() {
        return actionRepository.findAll().stream()
            .sorted(Comparator.comparing(PermissionAction::getId))
            .map(a -> Map.<String, Object>of(
                "id", a.getId(),
                "actionKey", a.getActionKey(),
                "description", a.getDescription() == null ? "" : a.getDescription()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNavigationForCurrentUser(List<String> roleNames) {
        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r));
        Map<String, Map<String, Boolean>> rolePermissions = getPermissionsForCurrentUser(roleNames);

        Set<String> allowedMenus = rolePermissions.entrySet().stream()
            .filter(e -> Boolean.TRUE.equals(e.getValue().get("list")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        List<MenuMaster> allMenus;
        if (isAdmin) {
            allMenus = menuRepository.findAll().stream()
                .filter(menu -> Boolean.TRUE.equals(menu.getActive()))
                .filter(menu -> menu.getSlug() != null)
                .filter(menu -> {
                    String slug = menu.getSlug().toLowerCase(Locale.ROOT);
                    Map<String, Boolean> menuPerm = rolePermissions.get(slug);
                    if (menuPerm == null || !menuPerm.containsKey("list")) {
                        return true;
                    }
                    return Boolean.TRUE.equals(menuPerm.get("list"));
                })
                .toList();
        } else {
            allMenus = menuRepository.findAll().stream()
                .filter(menu -> menu.getSlug() != null
                    && allowedMenus.contains(menu.getSlug().toLowerCase(Locale.ROOT))
                    && Boolean.TRUE.equals(menu.getActive()))
                .toList();
        }

        Map<Long, List<MenuMaster>> byModule = allMenus.stream()
            .collect(Collectors.groupingBy(m -> m.getModule().getId()));

        return moduleRepository.findAll().stream()
            .filter(module -> isAdmin || byModule.containsKey(module.getId()))
            .sorted(Comparator.comparing(ModuleMaster::getDisplayOrder))
            .map(module -> {
                List<Map<String, Object>> children = byModule
                    .getOrDefault(module.getId(), List.of())
                    .stream()
                    .sorted(Comparator.comparing(MenuMaster::getDisplayOrder))
                    .map(this::menuToNavChildMap)
                    .toList();
                return Map.<String, Object>of(
                    "title", module.getName(),
                    "segment", module.getSlug(),
                    "children", children
                );
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<String, Boolean>> getPermissionsForCurrentUser(List<String> roleNames) {
        List<Integer> roleIds = resolveRoleIdsByRoleNames(roleNames);
        Map<String, Map<String, Boolean>> merged = new HashMap<>();
        for (Integer roleId : roleIds) {
            Map<String, Map<String, Boolean>> rolePermissions = getPermissionsByRole(roleId);
            rolePermissions.forEach((menuKey, actions) -> {
                Map<String, Boolean> mergedActions = merged.computeIfAbsent(menuKey, key -> new HashMap<>());
                actions.forEach((action, allowed) -> {
                    if (Boolean.TRUE.equals(allowed)) {
                        mergedActions.put(action, true);
                    } else {
                        mergedActions.putIfAbsent(action, false);
                    }
                });
            });
        }
        return merged;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Map<String, Boolean>> getPermissionsByRole(Integer roleId) {
        if (permissionCache.containsKey(roleId)) {
            return permissionCache.get(roleId);
        }
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        for (RoleMenuPermission permission : permissionRepository.findByRole_Id(roleId)) {
            String menuSlug = permission.getMenu().getSlug().toLowerCase(Locale.ROOT);
            String action = permission.getAction().getActionKey().toLowerCase(Locale.ROOT);
            result.computeIfAbsent(menuSlug, k -> new HashMap<>()).put(action, Boolean.TRUE.equals(permission.getAllowed()));
        }
        permissionCache.put(roleId, result);
        return result;
    }

    @Override
    @Transactional
    public void updatePermissionsByRole(Integer roleId, PermissionUpdateRequest request) {
        RoleMaster role = roleMasterRepository.findById(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Map<String, Map<String, Boolean>> permissions = normalizePermissionPayload(
            request != null ? request.getPermissions() : null);
        // Execute delete as a single DML statement and flush before inserts to avoid
        // unique constraint races on (role_id, menu_id, action_id).
        permissionRepository.deleteByRoleIdInSingleQuery(roleId);
        permissionRepository.flush();

        if (!permissions.isEmpty()) {
            Map<String, MenuMaster> menuBySlug = menuRepository.findAll().stream()
                .collect(Collectors.toMap(
                    m -> m.getSlug().toLowerCase(Locale.ROOT),
                    m -> m,
                    (a, b) -> a
                ));
            Map<String, PermissionAction> actionByKey = actionRepository.findAll().stream()
                .collect(Collectors.toMap(a -> a.getActionKey().toLowerCase(), a -> a));

            permissions.forEach((menuSlug, actionMap) -> {
                MenuMaster menu = menuBySlug.get(menuSlug.toLowerCase(Locale.ROOT));
                if (menu == null) return;
                actionMap.forEach((actionKey, allowed) -> {
                    PermissionAction action = actionByKey.get(String.valueOf(actionKey).toLowerCase());
                    if (action == null) return;
                    RoleMenuPermission entity = new RoleMenuPermission();
                    entity.setRole(role);
                    entity.setMenu(menu);
                    entity.setAction(action);
                    entity.setAllowed(Boolean.TRUE.equals(allowed));
                    permissionRepository.save(entity);
                });
            });
        }

        evictRoleCache(roleId);
    }

    private static Map<String, Map<String, Boolean>> normalizePermissionPayload(Object raw) {
        if (raw == null) {
            return Map.of();
        }
        if (!(raw instanceof Map<?, ?> outer)) {
            return Map.of();
        }
        Map<String, Map<String, Boolean>> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> menuEntry : outer.entrySet()) {
            String menuSlug = String.valueOf(menuEntry.getKey()).toLowerCase(Locale.ROOT);
            Object innerVal = menuEntry.getValue();
            if (!(innerVal instanceof Map)) {
                continue;
            }
            Map<String, Boolean> actions = new LinkedHashMap<>();
            Map<?, ?> innerMap = (Map<?, ?>) innerVal;
            for (Map.Entry<?, ?> actionEntry : innerMap.entrySet()) {
                actions.put(
                    String.valueOf(actionEntry.getKey()).toLowerCase(Locale.ROOT),
                    toBoolean(actionEntry.getValue())
                );
            }
            result.put(menuSlug, actions);
        }
        return result;
    }

    private static boolean toBoolean(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean b) {
            return b;
        }
        if (o instanceof Number n) {
            return n.intValue() != 0;
        }
        if (o instanceof String s) {
            String t = s.trim();
            return "true".equalsIgnoreCase(t) || "1".equals(t) || "yes".equalsIgnoreCase(t);
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Integer roleId, String menuKey, String action) {
        if (menuKey == null || action == null) {
            return false;
        }
        String mk = menuKey.toLowerCase(Locale.ROOT);
        String act = action.toLowerCase(Locale.ROOT);
        return Boolean.TRUE.equals(
            getPermissionsByRole(roleId).getOrDefault(mk, Map.of()).get(act)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermissionByRoleNames(List<String> roleNames, String menuKey, String action) {
        List<Integer> roleIds = resolveRoleIdsByRoleNames(roleNames);
        return roleIds.stream().anyMatch(roleId -> hasPermission(roleId, menuKey, action));
    }

    @Override
    public void evictRoleCache(Integer roleId) {
        permissionCache.remove(roleId);
    }

    private Map<String, Object> menuToAdminMap(MenuMaster m) {
        List<MenuAction> pageActions = menuActionRepository.findByMenu_IdOrderByDisplayOrderAsc(m.getId());
        return menuToAdminMap(m, pageActions);
    }

    private Map<String, Object> menuToAdminMap(MenuMaster m, List<MenuAction> pageActions) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("name", m.getName());
        map.put("slug", m.getSlug());
        map.put("path", m.getPath());
        map.put("moduleId", m.getModule().getId());
        if (m.getParent() != null) {
            map.put("parentId", m.getParent().getId());
        }
        if (pageActions != null && !pageActions.isEmpty()) {
            map.put("pageActions", pageActions.stream().map(this::menuActionToMap).toList());
        }
        return map;
    }

    private Map<String, Object> menuActionToMap(MenuAction ma) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("actionKey", ma.getActionKey());
        row.put("label", ma.getLabel());
        if (ma.getEndpoint() != null && !ma.getEndpoint().isBlank()) {
            row.put("endpoint", ma.getEndpoint());
        }
        if (ma.getDisplayOrder() != null) {
            row.put("displayOrder", ma.getDisplayOrder());
        }
        return row;
    }

    private Map<String, Object> menuToNavChildMap(MenuMaster menu) {
        Map<String, Object> nav = new LinkedHashMap<>();
        nav.put("title", menu.getName());
        String segment = menu.getPath().startsWith("/") ? menu.getPath().substring(1) : menu.getPath();
        nav.put("segment", segment);
        nav.put("slug", menu.getSlug());
        List<MenuAction> pageActions = menuActionRepository.findByMenu_IdOrderByDisplayOrderAsc(menu.getId());
        if (!pageActions.isEmpty()) {
            nav.put("pageActions", pageActions.stream().map(this::menuActionToMap).toList());
        }
        return nav;
    }

    private void syncMenuPageActions(MenuMaster menu, List<MenuActionRequest> pageActions) {
        menuActionRepository.deleteAllByMenuId(menu.getId());
        if (pageActions == null || pageActions.isEmpty()) {
            return;
        }
        int order = 0;
        for (MenuActionRequest req : pageActions) {
            String key = normalizeActionKey(req.getActionKey());
            if (key.isBlank()) {
                continue;
            }
            String label = req.getLabel() != null && !req.getLabel().isBlank()
                ? req.getLabel().trim()
                : key;
            ensurePermissionActionExists(key, label);

            MenuAction entity = new MenuAction();
            entity.setMenu(menu);
            entity.setActionKey(key);
            entity.setLabel(label);
            if (req.getEndpoint() != null && !req.getEndpoint().isBlank()) {
                entity.setEndpoint(req.getEndpoint().trim());
            }
            entity.setDisplayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : order++);
            menuActionRepository.save(entity);
        }
    }

    private void ensurePermissionActionExists(String actionKey, String label) {
        PermissionAction action = actionRepository.findByActionKeyIgnoreCase(actionKey).orElseGet(() -> {
            PermissionAction created = new PermissionAction();
            created.setActionKey(actionKey);
            created.setDescription(label);
            return actionRepository.save(created);
        });
        if (label != null && !label.isBlank()
            && (action.getDescription() == null || action.getDescription().isBlank())) {
            action.setDescription(label);
            actionRepository.save(action);
        }
    }

    private static String normalizeActionKey(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
    }

    private List<Integer> resolveRoleIdsByRoleNames(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return List.of();
        }
        return roleNames.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(name -> !name.isBlank())
            .map(name -> roleMasterRepository.findByRoleNameIgnoreCase(name).map(RoleMaster::getId))
            .flatMap(Optional::stream)
            .distinct()
            .toList();
    }
}

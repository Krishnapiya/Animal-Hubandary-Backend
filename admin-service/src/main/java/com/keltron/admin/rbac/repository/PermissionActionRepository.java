package com.keltron.admin.rbac.repository;

import java.util.List;
import java.util.Optional;

import com.keltron.admin.rbac.entity.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionActionRepository extends JpaRepository<PermissionAction, Long> {
    List<PermissionAction> findByActionKeyIn(List<String> actionKeys);

    Optional<PermissionAction> findByActionKeyIgnoreCase(String actionKey);
}

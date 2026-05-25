package com.keltron.admin.rbac.repository;

import com.keltron.admin.rbac.entity.RoleMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleMenuPermissionRepository extends JpaRepository<RoleMenuPermission, Long> {
    List<RoleMenuPermission> findByRole_IdAndAllowedTrue(Integer roleId);
    List<RoleMenuPermission> findByRole_Id(Integer roleId);
    void deleteByRole_Id(Integer roleId);

    @Modifying
    @Query("delete from RoleMenuPermission p where p.role.id = :roleId")
    void deleteByRoleIdInSingleQuery(@Param("roleId") Integer roleId);
}

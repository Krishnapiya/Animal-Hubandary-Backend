package com.keltron.admin.rbac.repository;

import com.keltron.admin.rbac.entity.MenuMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuMasterRepository extends JpaRepository<MenuMaster, Long> {
    List<MenuMaster> findByModule_IdInAndActiveTrueOrderByDisplayOrderAsc(List<Long> moduleIds);
}

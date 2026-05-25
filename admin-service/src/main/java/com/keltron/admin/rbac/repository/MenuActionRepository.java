package com.keltron.admin.rbac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keltron.admin.rbac.entity.MenuAction;

public interface MenuActionRepository extends JpaRepository<MenuAction, Long> {
    List<MenuAction> findByMenu_IdOrderByDisplayOrderAsc(Long menuId);

    @Query("""
        SELECT ma FROM MenuAction ma
        JOIN FETCH ma.menu
        ORDER BY ma.menu.id ASC, ma.displayOrder ASC
        """)
    List<MenuAction> findAllWithMenuOrdered();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from MenuAction m where m.menu.id = :menuId")
    void deleteAllByMenuId(@Param("menuId") Long menuId);
}

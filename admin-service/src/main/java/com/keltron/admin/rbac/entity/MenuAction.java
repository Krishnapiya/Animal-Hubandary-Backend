package com.keltron.admin.rbac.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menu_actions", schema = "master")
public class MenuAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuMaster menu;

    /** RBAC action slug used in @RequirePermission and permission matrix (e.g. assign-roles). */
    @Column(name = "action_key", nullable = false)
    private String actionKey;

  @Column(nullable = false)
    private String label;

    /** Backend path or URL suffix for this button (e.g. admin/auth/master/users/{id}/assign-roles). */
    @Column
    private String endpoint;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}

package com.keltron.admin.rbac.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuRequest {
    private Long moduleId;
    private Long parentId;
    private String name;
    private String slug;
    private String path;
    /** Page-specific buttons/actions (action key + endpoint) for RBAC and UI */
    private List<MenuActionRequest> pageActions;
    private Integer displayOrder;
    private Boolean active;
}

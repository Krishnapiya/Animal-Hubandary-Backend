package com.keltron.admin.rbac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuActionRequest {
    /** Permission slug (lowercase, hyphenated), e.g. assign-roles */
    private String actionKey;
    /** Button label shown in UI and permission matrix column header */
    private String label;
    /** API path for this action (relative to gateway), e.g. admin/auth/master/users/{id}/assign-roles */
    private String endpoint;
    private Integer displayOrder;
}

package com.keltron.admin.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRolesRequest {
    /** Legacy: roles without office (stored as office_id NULL). */
    private List<Integer> roleIds;
    /** Preferred: roles grouped by office. */
    private List<OfficeRoleGroup> officeRoleGroups;
}

package com.keltron.admin.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeRoleGroup {
    /** Null = global / legacy assignment (no office scope). */
    private Integer officeId;
    private List<Integer> roleIds;
}

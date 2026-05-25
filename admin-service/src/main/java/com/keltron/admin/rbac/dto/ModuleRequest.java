package com.keltron.admin.rbac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleRequest {
    private String name;
    private String slug;
    private Integer displayOrder;
    private Boolean active;
}

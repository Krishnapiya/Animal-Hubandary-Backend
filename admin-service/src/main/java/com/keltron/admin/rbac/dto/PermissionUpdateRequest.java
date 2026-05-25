package com.keltron.admin.rbac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body for PATCH .../roles/{id}/permissions. {@code permissions} is deserialized as generic JSON
 * (nested maps) so clients sending 0/1 or "true" strings do not trigger Jackson 400 errors.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionUpdateRequest {
    private Object permissions;
}

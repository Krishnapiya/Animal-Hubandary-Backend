package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.ApplicationStatusMaster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusMasterDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String statusCode;
    private String statusName;
    private Integer displayOrder;
    private Boolean active;

    @Override
    public ApplicationStatusMaster toEntity() {
        ApplicationStatusMaster entity = new ApplicationStatusMaster();

        entity.setId(id);
        entity.setStatusCode(statusCode);
        entity.setStatusName(statusName);
        entity.setDisplayOrder(displayOrder);
        entity.setActive(active);

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null)
            return false;

        if (httpMethod.equals(HttpMethod.PATCH)) {
            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}
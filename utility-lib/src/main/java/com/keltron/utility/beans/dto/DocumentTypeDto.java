package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.DocumentType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentTypeDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String entityScope;
    private Boolean mandatory;
    private Boolean active;

    @Override
    public DocumentType toEntity() {

        DocumentType entity = new DocumentType();

        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);
        entity.setEntityScope(entityScope);
        entity.setMandatory(mandatory);
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
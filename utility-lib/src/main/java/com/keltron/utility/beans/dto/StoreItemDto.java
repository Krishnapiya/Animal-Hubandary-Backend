package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.StoreItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreItemDto extends AbstractDto {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;

    @Override
    public StoreItem toEntity() {
        StoreItem entity = new StoreItem();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {
        if (httpMethod == null) {
            return false;
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            if (!ValidationUtils.isValid(name)) {
                addError("name", name);
            }
        } else if (httpMethod.equals(HttpMethod.PATCH)) {
            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }
        }
        return getErrors() == null || getErrors().isEmpty();
    }
}

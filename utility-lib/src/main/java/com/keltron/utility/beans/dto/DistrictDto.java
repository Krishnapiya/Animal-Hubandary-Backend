package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.District;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistrictDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String code;
    private String name;
    private Boolean active;

    @Override
    public District toEntity() {

        District entity = new District();

        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);
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
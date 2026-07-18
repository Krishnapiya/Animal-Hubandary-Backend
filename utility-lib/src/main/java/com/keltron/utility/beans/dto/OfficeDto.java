package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.District;
import com.keltron.utility.jpa.entity.Office;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeDto extends AbstractDto {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String officeType;
    private String name;

    private Integer parentId;
    private String parentName;

    private Integer districtId;
    private String districtName;

    @SuppressWarnings("unchecked")
	@Override
    public Office toEntity() {

        Office entity = new Office();

        entity.setId(id);
        entity.setOfficeType(officeType);
        entity.setName(name);

        if (ValidationUtils.isValid(parentId)) {

            if (id != null && id.equals(parentId)) {
                entity.setParent(null);
            } else {
                Office parent = new Office();
                parent.setId(parentId);
                entity.setParent(parent);
            }

        } else {
            entity.setParent(null);
        }

        if (ValidationUtils.isValid(districtId)) {
            District district = new District();
            district.setId(districtId);
            entity.setDistrict(district);
        } else {
            entity.setDistrict(null);
        }

        return entity;
    }

    @Override
    public boolean isValid(HttpMethod httpMethod) {

        if (httpMethod == null) {
            return false;
        }

        if (httpMethod.equals(HttpMethod.POST)) {

            if (!ValidationUtils.isValid(officeType)) {
                addError("officeType", officeType);
            }

            if (!ValidationUtils.isValid(name)) {
                addError("name", name);
            }

            if (ValidationUtils.isValid(parentId)
                    && ValidationUtils.isValid(id)
                    && parentId.equals(id)) {

                addError("parentId", parentId);
            }

        } else if (httpMethod.equals(HttpMethod.PATCH)) {

            if (!ValidationUtils.isValid(id)) {
                addError("id", id);
            }

            if (ValidationUtils.isValid(parentId)
                    && parentId.equals(id)) {

                addError("parentId", parentId);
            }
        }

        return getErrors() == null || getErrors().isEmpty();
    }
}
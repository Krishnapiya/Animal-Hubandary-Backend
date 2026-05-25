package com.keltron.utility.beans.dto;

import org.springframework.http.HttpMethod;


import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.RoleMaster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMasterDto extends AbstractDto {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String roleName;

	@SuppressWarnings("unchecked")
	@Override
	public RoleMaster toEntity() {
		// TODO Auto-generated method stub
		RoleMaster entity = new RoleMaster();
		entity.setId(id);
		entity.setRoleName(roleName);
		return entity;
	}

	@Override
	public boolean isValid(HttpMethod httpMethod) {
		if (httpMethod == null)
			return false; // Prevent null errors

		if (httpMethod.equals(HttpMethod.POST)) {
//			if (!ValidationUtils.isValid(roleName)) {
//				addError("roleName", roleName);
//			}
		} else if (httpMethod.equals(HttpMethod.PATCH)) {
			if (!ValidationUtils.isValid(id)) {
				addError("id", id);
			}
		}

		return getErrors() == null || getErrors().isEmpty();
	}

}


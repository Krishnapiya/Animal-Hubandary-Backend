package com.keltron.utility.beans.abs;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.keltron.utility.jpa.entity.AbstractEntity;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractDto implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -436929380206170419L;

	
	protected String createdBy;

	
	protected Long createdAt;

	// @JsonIgnore
	protected String lastModifiedBy;

	// @JsonIgnore
	protected Long lastModifiedAt;

	
	@Parameter(hidden = true)
	private Set<String> errors;

	public abstract <T extends AbstractEntity> T toEntity();

	@Parameter(hidden = true)
	public abstract boolean isValid(HttpMethod httpMethod);

	protected void addError(String errorParamKey, Object errorParamValue) {
		if (errors == null) {
			errors = new HashSet<>();
		}
		errors.add("Please enter the field " + errorParamKey);
	}

	protected void addError(String errorMessage) {
		if (errors == null) {
			errors = new HashSet<>();
		}
		errors.add(errorMessage);
	}

	protected void addError(Set<String> errorList) {
		if (errors == null) {
			errors = new HashSet<>();
		}
		errors.addAll(errorList);
	}
}

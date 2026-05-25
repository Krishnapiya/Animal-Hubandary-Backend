package com.keltron.utility.requests.bean;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultimediaBean extends AbstractDto {

	/**
	 *
	 */
	private static final long serialVersionUID = -5382195969892405732L;

	private Long id;

	private String contentType;

	private Long fileSizeKb;

	private String resourceName;

	private String fileExtn;

	private String resourceDescription;

	private String base64EncodedData;

	@Override
	public <T extends AbstractEntity> T toEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(HttpMethod httpMethod) {
		if (!ValidationUtils.isValid(contentType)) {
			addError("contentType", contentType);
		}
		if (!ValidationUtils.isValid(fileSizeKb)) {
			addError("fileSizeKb", fileSizeKb);
		}
		if (!ValidationUtils.isValid(resourceName)) {
			addError("resourceName", resourceName);
		}
		if (!ValidationUtils.isValid(contentType)) {
			addError("contentType", contentType);
		}
		if (!ValidationUtils.isValid(fileExtn)) {
			addError("fileExtn", fileExtn);
		}
		if (!ValidationUtils.isValid(resourceDescription)) {
			addError("resourceDescription", resourceDescription);
		}
		if (!ValidationUtils.isValid(base64EncodedData)) {
			addError("base64EncodedData", base64EncodedData);
		}
		return !ValidationUtils.isValid(getErrors());
	}
}

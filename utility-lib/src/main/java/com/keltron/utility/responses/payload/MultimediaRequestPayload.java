package com.keltron.utility.responses.payload;



import java.util.List;

import org.springframework.http.HttpMethod;

import com.keltron.utility.ValidationUtils;
import com.keltron.utility.beans.abs.AbstractDto;
import com.keltron.utility.jpa.entity.AbstractEntity;
import com.keltron.utility.requests.bean.MultimediaBean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class MultimediaRequestPayload extends AbstractDto {

	/**
	 *
	 */
	private static final long serialVersionUID = -2140998744350364757L;

	private List<MultimediaBean> multimediaList;

	public MultimediaRequestPayload(List<MultimediaBean> multimediaList) {
		this.multimediaList = multimediaList;
	}

	@Override
	public <T extends AbstractEntity> T toEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(HttpMethod httpMethod) {
		// TODO Auto-generated method stub
		if (!ValidationUtils.isValid(multimediaList)) {
			addError("multimediaList", multimediaList);
		}
		if (ValidationUtils.isValid(multimediaList)) {
			multimediaList.stream().forEach(item -> {
				if (!item.isValid(httpMethod)) {
					addError(item.getErrors());
				}
			});
		}
		return !ValidationUtils.isValid(getErrors());
	}

	
}

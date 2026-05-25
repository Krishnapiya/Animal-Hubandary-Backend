package com.keltron.utility.requests;

import java.io.Serializable;

import com.keltron.utility.ValidationUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author krishnapriya
 *
 * @param <T>
 */
@NoArgsConstructor
@ToString
public @Data class Request<T> implements Serializable {

	

	/**
	 *
	 */
	private static final long serialVersionUID = 2467164618615108395L;

	

	private T payLoad;

	public Request(T payLoad) {
		this.payLoad = payLoad;
	}

	/**
	 *
	 * @return
	 */
	public boolean isValid() {
		return ValidationUtils.isValid(payLoad);
	}

	
}

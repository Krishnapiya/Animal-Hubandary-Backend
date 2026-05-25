package com.keltron.utility.responses;


import java.io.Serializable;

import com.keltron.utility.constants.response.ResultCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class AbstractResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3729463435935762913L;

	@NonNull
	protected ResultCode resultCode;

	protected String resultString;

	public AbstractResponse(ResultCode resultCode, String resultString) {
		this.resultCode = resultCode;
		this.resultString = resultString;
	}
}

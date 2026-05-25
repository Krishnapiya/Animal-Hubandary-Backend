package com.keltron.utility.responses;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.keltron.utility.constants.response.ResultCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestException extends RuntimeException implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4234756587212959185L;

	private String loggerMessage;

	private Object[] args;

	private Object additionalData;

	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	
	private ResultCode resultCode;

	public RestException(String loggerMessage) {
		super(loggerMessage);
	}

	public RestException(String loggerMessage, Object[] args) {
		super(loggerMessage);
		this.args = args;
	}

	public RestException(String loggerMessage, Object additionalData) {
		super(loggerMessage);
		this.additionalData = additionalData;
	}

	public RestException(String loggerMessage, HttpStatus status) {
		super(loggerMessage);
		this.status = status;
	}

	public RestException(String loggerMessage, Object[] args, HttpStatus status) {
		super(loggerMessage);
		this.args = args;
		this.status = status;
	}

	public RestException(String loggerMessage, Object additionalData, HttpStatus status) {
		super(loggerMessage);
		this.additionalData = additionalData;
		this.status = status;
	}
}

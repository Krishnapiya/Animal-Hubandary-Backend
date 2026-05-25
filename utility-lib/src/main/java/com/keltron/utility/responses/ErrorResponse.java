package com.keltron.utility.responses;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.keltron.utility.constants.ResultString;
import com.keltron.utility.constants.response.ResultCode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends AbstractResponse {
	/**
	 *
	 */
	private static final long serialVersionUID = 208342465574481738L;

	private HttpStatus status;
	private Set<String> errors;
	private Object[] args;
	private Object additionalData;
	private Integer errorCode;

	public ErrorResponse() {
		super(ResultCode.COMM_OPERATION_FAILURE, ResultString.COMM_OPERATION_FAILURE);
	}

	/**
	 *
	 * @param status
	 * @param message
	 * @param error
	 */
	public ErrorResponse(HttpStatus status, String message, String error) {
		super(ResultCode.COMM_OPERATION_FAILURE, message);
		this.status = status;
		errors = new HashSet<>(Arrays.asList(error));
	}

	/**
	 *
	 * @param status
	 * @param message
	 */
	public ErrorResponse(HttpStatus status, String message) {
		super(ResultCode.COMM_OPERATION_FAILURE, message);
		this.status = status;
	}

	/**
	 *
	 * @param errorCode
	 * @param message
	 */
	public ErrorResponse(Integer errorCode, String message) {
		super(ResultCode.COMM_OPERATION_FAILURE, message);
		this.errorCode = errorCode;
	}

	public ErrorResponse(HttpStatus status, String message, Set<String> errors) {
		super(ResultCode.COMM_OPERATION_FAILURE, message);
		this.status = status;
		this.errors = errors;
	}

	public ErrorResponse(HttpStatus status, String message, Set<String> errors, Exception ex) {
		super(ResultCode.COMM_OPERATION_FAILURE, message);
		this.status = status;
		this.errors = errors;
	}
	
	  public ErrorResponse(ResultCode resultCode, HttpStatus status, String message, String error) {
	        super(resultCode, message);
	        this.status = status;
	        errors = new HashSet<>(Arrays.asList(error));
	    }
}

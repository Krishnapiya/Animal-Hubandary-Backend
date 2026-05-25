package com.keltron.utility;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.keltron.utility.constants.ResultString;
import com.keltron.utility.constants.response.ResultCode;
import com.keltron.utility.responses.AbstractResponse;
import com.keltron.utility.responses.ErrorResponse;
import com.keltron.utility.responses.RestResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic Response Builder class to construct standardized API responses. 
 * It supports success and error responses, allowing method chaining for customization.
 * 
 * @param <T> The type of data included in a success response.
 */
public class ResponseBuilder {

	private RestResponse<Object> restResponse;

	private ErrorResponse errorResponse;

	private HttpStatus status;

	/**
	 *
	 * @param data
	 * @return
	 */
	public ResponseBuilder withData(Object data) {
		this.restResponse = new RestResponse<>(ResultCode.COMM_OPERATION_SUCCESS, ResultString.COMM_OPERATION_SUCCESS);
		restResponse.setPayLoad(data);
		return this;
	}

	/**
	 *
	 * @param data
	 * @param skipPayload
	 * @return
	 */
	public ResponseBuilder withData(Object data, boolean skipPayload) {
		this.restResponse = new RestResponse<>(ResultCode.COMM_OPERATION_SUCCESS, ResultString.COMM_OPERATION_SUCCESS);
		if (!skipPayload) {
			restResponse.setPayLoad(data);
		}
		return this;
	}

	/**
	 *
	 * @param errorMessage
	 * @return
	 */
	public ResponseBuilder withError(String errorMessage) {
		this.status = HttpStatus.OK;
		this.restResponse = new RestResponse<>(ResultCode.COMM_OPERATION_FAILURE, errorMessage);
		return this;
	}

	/**
	 *
	 * @param status
	 * @param errorMessage
	 * @return
	 */

	public ResponseBuilder withError(HttpStatus httpStatus, String errorMessage) {
		this.status = httpStatus;
		String errMsg = switch (httpStatus) {
		case BAD_REQUEST -> ResultString.BAD_REQUEST;
		case INTERNAL_SERVER_ERROR -> ResultString.INTERNAL_SERVER_ERROR;
		default -> ResultString.COMM_OPERATION_FAILURE;
		};
		this.errorResponse = new ErrorResponse(httpStatus, errMsg, errorMessage);
		return this;
	}

	/**
	 *
	 * @param errorCode
	 * @param errorMessage
	 * @return
	 */
	public ResponseBuilder withError(Integer errorCode, String errorMessage) {
		this.status = HttpStatus.UNAUTHORIZED;
		this.errorResponse = new ErrorResponse(errorCode, errorMessage);
		return this;
	}

	public ResponseBuilder withError(HttpStatus httpStatus, Set<String> errorMessage) {
		this.status = httpStatus;
		String errMsg = switch (httpStatus) {
		case BAD_REQUEST -> ResultString.BAD_REQUEST;
		case INTERNAL_SERVER_ERROR -> ResultString.INTERNAL_SERVER_ERROR;
		default -> ResultString.COMM_OPERATION_FAILURE;
		};
		this.errorResponse = new ErrorResponse(httpStatus, errMsg, errorMessage);
		return this;
	}

	public ResponseBuilder withError(HttpStatus httpStatus) {
		this.status = httpStatus;
		String errMsg = switch (httpStatus) {
		case BAD_REQUEST -> ResultString.BAD_REQUEST;
		case INTERNAL_SERVER_ERROR -> ResultString.INTERNAL_SERVER_ERROR;
		default -> ResultString.COMM_OPERATION_FAILURE;
		};
		this.errorResponse = new ErrorResponse(httpStatus, errMsg, errMsg);
		return this;
	}

	public ResponseEntity<AbstractResponse> build() {
		return restResponse != null ? ResponseEntity.ok(restResponse)
				: ResponseEntity.status(status).body(errorResponse);
	}
}
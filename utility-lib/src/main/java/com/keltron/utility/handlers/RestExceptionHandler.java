package com.keltron.utility.handlers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.keltron.utility.constants.RestExceptionCodes;
import com.keltron.utility.constants.logging.LogState;
import com.keltron.utility.constants.response.ResultCode;
import com.keltron.utility.logging.LogStringBuilder;
import com.keltron.utility.responses.ErrorResponse;
import com.keltron.utility.responses.RestException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<ErrorResponse> handleDataIntegrityConstraintViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		logger.error(LogStringBuilder.getLoggerString(LogState.EXCEPTION, getClass(),
				"handleDataIntegrityConstraintViolation", null), ex);
		Set<String> errors = new HashSet<String>();
		String message = RestExceptionCodes.ALREADY_EXISTS;
		errors.add(message);
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, message, errors);
		return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
	}
	
	@ExceptionHandler({ RestException.class })
	public ResponseEntity<ErrorResponse> handleRestException(RestException ex, WebRequest request) {
		logger.error(LogStringBuilder.getLoggerString(LogState.EXCEPTION, getClass(), "handleRestException", null), ex);
		Set<String> errors = new HashSet<String>();
		String message = ex.getMessage();
		errors.add(message);
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, message, errors);
		return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
	}

	/**
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ErrorResponse> handleAll(final Exception ex, final WebRequest request) {
		logger.error(LogStringBuilder.getLoggerString(LogState.EXCEPTION, getClass(), "handleAll", null), ex);
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
	}


}

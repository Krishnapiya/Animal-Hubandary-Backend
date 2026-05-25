
 
package com.keltron.utility.constants.response;

import com.keltron.utility.constants.ResultString;

/**
 *
 * @author krishnapriya
 *
 */
public enum ResultCode {

	COMM_OPERATION_SUCCESS(ResultString.COMM_OPERATION_SUCCESS),
	COMM_OPERATION_FAILURE(ResultString.COMM_OPERATION_FAILURE);

	private final String value;

	private ResultCode(String value) {
		this.value = value;
	}

	/**
	 *
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 *
	 * @param code
	 * @return
	 */
	public static ResultCode getTypeByValue(String code) {
		for (ResultCode status : ResultCode.values()) {
			if (code == status.value) {
				return status;
			}
		}
		return null;
	}

}

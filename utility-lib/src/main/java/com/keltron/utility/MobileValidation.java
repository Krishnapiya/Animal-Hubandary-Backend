package com.keltron.utility;

public class MobileValidation {

	/**
	 *
	 * @param orginalString
	 * @return
	 */
	public static boolean isMobileValid(Long mobile) {
		if (mobile == null) {
			return false;
		}
		return ValidationUtils.isValidMobile(String.valueOf(mobile));

	}

}

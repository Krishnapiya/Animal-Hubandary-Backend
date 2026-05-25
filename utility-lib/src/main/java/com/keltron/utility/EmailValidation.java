package com.keltron.utility;

import java.util.regex.Pattern;

public class EmailValidation {

	/**
	 *
	 * @param orginalString
	 * @return
	 */
	public static boolean isEmailValid(String email) {
		return ValidationUtils.isValidEmail(email);
	}

	public static boolean patternMatches(String emailAddress, String regexPattern) {
		if (emailAddress == null || regexPattern == null) {
			return false;
		}
		return Pattern.compile(regexPattern).matcher(emailAddress).matches();
	}
}

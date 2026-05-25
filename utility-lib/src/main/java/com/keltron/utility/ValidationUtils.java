package com.keltron.utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("\\d{10}");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Checks if an object is not null.
     */
    public static boolean isValid(Object targetObject) {
        return targetObject != null;
    }

    /**
     * Checks if a string is not null, not empty, and not just whitespace.
     */
    public static boolean isValid(String targetString) {
        return targetString != null && !targetString.strip().isEmpty();
    }

    /**
     * Checks if a collection is not null and not empty.
     */
    public static boolean isValid(Collection<?> targetCollection) {
        return targetCollection != null && !targetCollection.isEmpty();
    }

    /**
     * Checks if a map is not null and not empty.
     */
    public static boolean isValid(Map<?, ?> targetMap) {
        return targetMap != null && !targetMap.isEmpty();
    }

    /**
     * Checks if an array is not null and has at least one element.
     */
    public static boolean isValid(Object[] targetArray) {
        return targetArray != null && targetArray.length > 0;
    }

    /**
     * Checks if a number (Integer, Long, Double) is not null and greater than zero.
     */
    public static boolean isValid(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    /**
     * Checks if an integer is valid and includes zero as a valid value.
     */
    public static boolean isValidIntegerIncZero(Integer intValue) {
        return intValue != null && intValue >= 0;
    }

    /**
     * Checks if a given string is a valid integer.
     */
    public static boolean isValidInteger(String intString) {
        try {
            Integer.parseInt(intString.strip());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Checks if a given string is a valid double.
     */
    public static boolean isValidDouble(String doubleString) {
        try {
            Double.parseDouble(doubleString.strip());
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Checks if a given string is a valid mobile number (10-digit format).
     */
    public static boolean isValidMobile(String mobileNo) {
        return mobileNo != null && MOBILE_PATTERN.matcher(mobileNo.strip()).matches();
    }

    /**
     * Checks if a given string is a valid email.
     */
    public static boolean isValidEmail(String emailId) {
        return emailId != null && EMAIL_PATTERN.matcher(emailId.strip()).matches();
    }
}

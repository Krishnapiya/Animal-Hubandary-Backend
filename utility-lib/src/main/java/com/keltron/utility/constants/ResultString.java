package com.keltron.utility.constants;


/**
 *
 * @author krishnapriya
 *
 */
public final class ResultString {

    public static final String COMM_OPERATION_SUCCESS = "Operation successful";

    public static final String COMM_OPERATION_FAILURE = "Operation failed";

    public static final String NO_DATA_AVAILABLE = "No data available";

    public static final String BAD_REQUEST = "Invalid request payload";

    public static final String INTERNAL_SERVER_ERROR = 
        "An internal error occurred. Please try again later or contact support if the issue persists.";

    private ResultString() {
        // Private constructor to prevent instantiation
    }
}

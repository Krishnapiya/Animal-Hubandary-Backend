package com.keltron.utility;

import java.sql.Timestamp;
import java.util.Date;

public class ConversionUtils {

	public static Long toLong(Timestamp source) {

		return source != null ? source.getTime() : null;

	}

	public static Long toLong(Date source) {

		return source != null ? source.getTime() : null;

	}

}

package io.github.mooeypoo.timelyactions.utils;

import com.google.common.base.Strings;

import io.github.mooeypoo.timelyactions.config.interfaces.IntervalSectionConfigInterface;

public class ValidityHelper {

	private ValidityHelper() {
		// utility class should never be contructed.
	}

	public static Boolean isStringEmpty(String str) {
		return str == null || str.isBlank();
	}
	
	public static Boolean isIntervalConfigValid(IntervalSectionConfigInterface intervalData) {
		return intervalData.every_minutes() > 0 && !intervalData.commands().isEmpty();
	}

	/**
	 * Check if a given string is an integer.
	 */
	public static int getInt(String str) {
		int i;
	    if (str == null) {
	        return -1;
	    }
	    try {
	        i = Integer.parseInt(str);
	    } catch (NumberFormatException nfe) {
	        return -1;
	    }
	    return i;
	}
}

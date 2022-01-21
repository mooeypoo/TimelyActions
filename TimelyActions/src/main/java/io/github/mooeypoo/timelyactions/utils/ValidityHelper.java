package io.github.mooeypoo.timelyactions.utils;

import io.github.mooeypoo.timelyactions.config.interfaces.IntervalSectionConfigInterface;

public class ValidityHelper {
	public static Boolean isStringEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static Boolean isIntervalConfigValid(IntervalSectionConfigInterface intervalData) {
		return intervalData.every_minutes() > 0 && !intervalData.commands().isEmpty();
	}

	/**
	 * Check if a given string is an integer
	 * @param s
	 * @param radix
	 * @return
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

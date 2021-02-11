package io.github.mooeypoo.timelyactions.utils;

import io.github.mooeypoo.timelyactions.config.interfaces.IntervalSectionConfigInterface;

public class ValidityHelper {
	public static Boolean isStringEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static Boolean isIntervalConfigValid(IntervalSectionConfigInterface intervalData) {
		return intervalData.every_minutes() > 0 && !intervalData.commands().isEmpty();
	}
}

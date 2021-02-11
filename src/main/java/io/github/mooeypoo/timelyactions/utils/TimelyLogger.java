package io.github.mooeypoo.timelyactions.utils;

import java.util.logging.Logger;

public class TimelyLogger {
	// Singleton
	private static TimelyLogger single_instance = null;
	private Logger pluginLogger;
	private Boolean showRobust = false;
	
	private TimelyLogger() {
		
	}
	
	public void setShowRobust(Boolean isRobust) {
		this.showRobust = isRobust;
	}
	
	public void setPluginLogger(Logger logger) {
		this.pluginLogger = logger;
	}

	public void warn(String str) {
		// Always show warnings
		this.warn(str, true);
	}

	public void info(String str) {
		this.info(str, this.showRobust);
	}

	public void warn(String str, Boolean alwaysOutput) {
		if (alwaysOutput) {
			this.pluginLogger.warning(str);
		}
	}

	public void info(String str, Boolean alwaysOutput) {
		if (alwaysOutput) {
			this.pluginLogger.info(str);
		}
	}

	
	public static TimelyLogger getInstance() {
		if (single_instance == null) {
			single_instance = new TimelyLogger();
		}
		return single_instance;
	}
}

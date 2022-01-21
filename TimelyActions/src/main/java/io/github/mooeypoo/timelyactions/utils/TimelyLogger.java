package io.github.mooeypoo.timelyactions.utils;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

	public void outputToPlayerOrConsole(String out, CommandSender sender) {
		this.output(out, sender, false);
	}
	
	public void outputToPlayerAndConsole(String out, CommandSender sender) {
		this.output(out, sender, true);
	}

	private void output(String out, CommandSender sender, Boolean sendToBoth) {
		String formatColor = ChatColor.BLUE + "[TimelyActions] " + ChatColor.WHITE + "%s";
		String formatBlank = "%s";
		Boolean inGame = sender instanceof Player;
		
		if (sendToBoth || !inGame) {
			this.pluginLogger.info(String.format(formatBlank, out));
		}
		
		if (inGame) {
			// Send to the player, in-game
			// Only do that if this was invoked from in-game
			sender.sendMessage(String.format(formatColor, out));
		}
		
	}
}

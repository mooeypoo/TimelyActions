package io.github.mooeypoo.timelyactions.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mooeypoo.timelyactions.TimelyActions;
import io.github.mooeypoo.timelyactions.database.LogDatabase;
import io.github.mooeypoo.timelyactions.database.LogItem;
import io.github.mooeypoo.timelyactions.managers.ProcessManager;
import io.github.mooeypoo.timelyactions.utils.TimelyLogger;
import io.github.mooeypoo.timelyactions.utils.ValidityHelper;

public class TimelyActionsCommandExecutor implements CommandExecutor {
	private static Integer DEFAULT_RESULT_LIMIT = 5;
	private TimelyActions plugin;
	private HashMap<String, String> paramMap = new HashMap<>();
	private TimelyLogger logger;
	private LogDatabase logDB;
	private ProcessManager manager;

	public TimelyActionsCommandExecutor(TimelyActions plugin) {
		this.plugin = plugin;
		this.logger = TimelyLogger.getInstance();
		this.logDB = new LogDatabase(this.plugin);
		this.manager = this.plugin.getProcessManager();

		this.generateParameterMap();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("timelyactions")) {
			return false;
		}

		if (args.length == 0 || args[0].equalsIgnoreCase("help") ) {
			this.outputHelp(sender);
			return true;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("timelyactions.cmd.reload")) {
				this.logger.outputToPlayerOrConsole("You do not have permission to invoke the reload action.", sender);
				return true;
			}
			if (!this.manager.isRunning()) {
				this.logger.outputToPlayerOrConsole("Reload is unnecessary if the timed action is not running. Use `/timelyactions start` to restart the process with whatever changes to your config.", sender);
				return true;
			}
			this.logger.outputToPlayerOrConsole("Stopping timed evaluation...", sender);
			this.manager.stop();
			this.logger.outputToPlayerOrConsole("Reloading configuration...", sender);
			this.manager.initialize();
			this.logger.outputToPlayerOrConsole(String.format("Configuration reloaded. Timed evaluation running with %d interval(s).", this.manager.getIntervalNames().size()), sender);
		} else if (args[0].equalsIgnoreCase("stop")) {
			if (!sender.hasPermission("timelyactions.cmd.reload")) {
				this.logger.outputToPlayerOrConsole("You do not have permission to invoke the stop action.", sender);
				return true;
			}
			if (!this.manager.isRunning()) {
				this.logger.outputToPlayerOrConsole("Timed process is already stopped.", sender);
				return true;
			}
			this.logger.outputToPlayerOrConsole("Stopping timed evaluation...", sender);
			this.manager.stop();
			this.logger.outputToPlayerOrConsole("Timed evaluation stopped.", sender);
		} else if (args[0].equalsIgnoreCase("start")) {
			if (!sender.hasPermission("timelyactions.cmd.reload")) {
				this.logger.outputToPlayerOrConsole("You do not have permission to invoke the start action.", sender);
				return true;
			}
			if (this.manager.isRunning()) {
				this.logger.outputToPlayerOrConsole("Timed process is already running.", sender);
				return true;
			}
			this.logger.outputToPlayerOrConsole("Starting timed evaluation...", sender);
			this.manager.initialize();
			this.logger.outputToPlayerOrConsole("Timed evaluation started.", sender);
		} else if (args[0].equalsIgnoreCase("player")) {
			if (!sender.hasPermission("timelyactions.cmd.player")) {
				this.logger.outputToPlayerOrConsole("You do not have permission to invoke the player action.", sender);
				return true;
			}
			if (args.length < 2 || ValidityHelper.isStringEmpty(args[1])) {
				this.logger.outputToPlayerOrConsole("Missing player name. Usage: /timelyactions player [player name] [optional: number of results]", sender);
				return false;
			}
			int resultNum = DEFAULT_RESULT_LIMIT;
			if (args.length == 3) {
				int numFromString = ValidityHelper.getInt(args[2]);
				if (numFromString <= 0 || numFromString > 50) {
					// Invalid
					this.logger.outputToPlayerOrConsole("Given result number is invalid. Using default (" + DEFAULT_RESULT_LIMIT + ")", sender);
				} else {
					resultNum = numFromString;
				}
			}
			
			String player = args[1];
			List<LogItem> results = this.logDB.getLogsForPlayer(player, resultNum);
			if (results == null || results.size() == 0) {
				this.logger.outputToPlayerOrConsole(String.format("No results found for player '%s'", player), sender);
				return true;
			}
			
			// Show results
			this.logger.outputToPlayerOrConsole(String.format("Showing %d results for player '%s'", resultNum, player), sender);
			for (LogItem data : results) {
				this.logger.outputToPlayerOrConsole(
					String.format("* [%s] -> Interval '%s'.", data.runTime(), data.interval()),
					sender
				);
			}
			this.logger.outputToPlayerOrConsole("To check on a specific interval for the user, use /timelyactions playerinterval [player name] [interval]", sender);
		} else if (args[0].equalsIgnoreCase("playerinterval")) {
			if (!sender.hasPermission("timelyactions.cmd.playerinterval")) {
				this.logger.outputToPlayerOrConsole("You do not have permission to invoke the playerinterval action.", sender);
				return true;
			}
			if (args.length < 3 || ValidityHelper.isStringEmpty(args[1]) || ValidityHelper.isStringEmpty(args[2])) {
				this.logger.outputToPlayerOrConsole("Missing parameter. Usage: /timelyactions playerinterval [player name] [interval name]", sender);
				return false;
			}
			int resultNum = DEFAULT_RESULT_LIMIT;
			if (args.length == 4) {
				int numFromString = ValidityHelper.getInt(args[3]);
				if (numFromString <= 0 || numFromString > 50) {
					// Invalid
					this.logger.outputToPlayerOrConsole("Given result number is invalid. Using default (" + DEFAULT_RESULT_LIMIT + ")", sender);
				} else {
					resultNum = numFromString;
				}
			}
			
			String player = args[1];
			String interval = args[2];

			// Show results
			List<LogItem> results = this.logDB.getLogsForPlayerInterval(player, interval, resultNum);
			if (results == null || results.size() == 0) {
				this.logger.outputToPlayerOrConsole(String.format("No results found for interval '%s' for player '%s'", interval, player), sender);
				return true;
			}

			// Show results
			this.logger.outputToPlayerOrConsole(String.format("Showing %d latest run times for interval '%s' for player '%s'", resultNum, interval, player), sender);
			for (LogItem data : results) {
				this.logger.outputToPlayerOrConsole(
					String.format("* [%s] -> Interval '%s'.", data.runTime(), data.interval()),
					sender
				);
			}
			this.logger.outputToPlayerOrConsole("To check on a specific interval for the user, use /timelyactions playerinterval [player name] [interval]", sender);
		}
		
		return true;
	}

	private void outputHelp(CommandSender sender) {
		String output = "";
		boolean toPlayer = (sender instanceof Player);
		
		output = "Available actions for the /timelyactions command:";
		
		if (sender instanceof Player) {
			// Send to the player, in-game
			sender.sendMessage(output);
		} else {
			this.plugin.getLogger().info(output);
		}

		this.logger.outputToPlayerOrConsole("Commands list:", sender);

		for (Map.Entry<String, String> param : this.paramMap.entrySet()) {
			output = String.format(
					toPlayer ? ("* " + ChatColor.GREEN + "%s" + ChatColor.WHITE + ": %s") : "* %s: %s",
					param.getKey(), param.getValue()
			);
			this.logger.outputToPlayerOrConsole(output, sender);
		}
		this.logger.outputToPlayerOrConsole("Use the command /timelyactions [action] to invoke any of the above actions.", sender);
	}

	private void generateParameterMap() {
		this.paramMap.clear();
		this.paramMap.put("reload", "Reload interval data based on changes to the config.");
		this.paramMap.put("stop", "Stop the timed process of all intervals.");
		this.paramMap.put("start", "Start the timed process of all intervals.");
		this.paramMap.put("player", "Check the logs for the latest intervals the requested player has had run, and their dates and times. Call with /timelyactions player [playername] (optional number of results)");
		this.paramMap.put("playerinterval", "Check the logs for a specific interval for the requested user. Call with /timelyactions playerinterval [playername] [intervalname] (optional number of results)");
	}
}

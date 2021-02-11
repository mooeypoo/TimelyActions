package io.github.mooeypoo.timelyactions.managers;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.github.mooeypoo.timelyactions.TimelyActions;
import io.github.mooeypoo.timelyactions.config.ConfigHandler;
import io.github.mooeypoo.timelyactions.config.interfaces.IntervalSectionConfigInterface;
import io.github.mooeypoo.timelyactions.config.interfaces.MainConfigInterface;
import io.github.mooeypoo.timelyactions.database.Database;
import io.github.mooeypoo.timelyactions.store.IntervalStore;
import io.github.mooeypoo.timelyactions.store.PlayerStore;
import io.github.mooeypoo.timelyactions.store.items.DatabaseItem;
import io.github.mooeypoo.timelyactions.utils.TimelyLogger;
import io.github.mooeypoo.timelyactions.utils.ValidityHelper;

public class ProcessManager {
	private static DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private TimelyActions plugin;
	private ConfigHandler config;
	private IntervalStore intervalStore = new IntervalStore();
	private PlayerStore playerStore = new PlayerStore();
	private BukkitTask intervalTask;
	private Database database;
	private TimelyLogger logger = TimelyLogger.getInstance();

	public ProcessManager(TimelyActions plugin) {
		this.plugin = plugin;
		this.config = new ConfigHandler(this.plugin);
		this.database = new Database(this.plugin);
	}

	public void initialize() {
		this.database.initialize();

		// Load data
		this.reload();
		
		// Get initial values from the db
		// Skip this for now; we'll gather
		// the data when players join
		//		this.loadDataFromDatabase();
		
		// Start the task
		this.startIntervalTask();
	}

	public void stop() {
		// Stop the task
		this.intervalTask.cancel();

		// Save everything to the DB
		this.saveAllDataToDatabase();
	}

	public void reload() {
		// Reset data
		this.intervalStore.reset();
		this.playerStore.reset();

		// Read the config
		try {
			config.reload();
		} catch (Exception e) {
			this.logger.warn("Error reading configuration: " + e.getMessage());
			this.logger.warn("TimelyActions did not load properly. Please review your configuration files and restart the plugin.");
			return;
		}

		// Build the interval store from the config
		MainConfigInterface configData = null;
		try {
			configData = this.config.getData();
		} catch (Exception e) {
			this.logger.warn("Problem reading the main config for TimelyActions. Plugin will not work properly; please check your config files and reload the plugin.");
			return;
		}
		
		// Update logger level
		this.logger.setShowRobust(configData.log_everything());
		
		for (String intervalName : configData.intervals().keySet()) {
			IntervalSectionConfigInterface intervalData = configData.intervals().get(intervalName);

			// Make sure the data is valid
			if (!ValidityHelper.isIntervalConfigValid(intervalData)) {
				continue;
			}
			
			// Add to store
			this.intervalStore.addInterval(
				intervalName,
				intervalData.every_minutes(),
				intervalData.commands(),
				intervalData.permission(),
				intervalData.message_to_user()
			);
		}
	}

	public void addPlayer(String playerName) {
		// If player doesn't exist in the store, add the player
		// and load the data from the db
		if (!this.playerStore.doesPlayerExist(playerName)) {
			this.playerStore.addPlayer(playerName);
			
			// Get info from DB
			this.loadPlayerDataFromDatabase(this.database.getSpecificPlayerRecords(playerName));
		}
	}
	/**
	 * Check all intervals for all players, and get a list of
	 * runnable commands; update the last-run for players if
	 * their commands are included to be run.
	 *
	 * @return A list of commands that should run now
	 */
	public void processAllIntervals() {
		// Process for all online players
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Make sure player is in store (no-op if the player already exists in store)
			this.addPlayer(player.getName());

			// Check all intervals
			for(String intervalName : this.intervalStore.getIntervalNames()) {
				// Check that the user has matching permissions
				if (!this.intervalStore.doesPlayerHaveIntervalPermission(player, intervalName)) {
					// User does not have permission
					// this.logger.info(String.format("Player '%s' skipped for interval '%s': No permission for this interval.", player.getName(), intervalName));
					continue;
				}
				
				// Check if the user's last-run is outside the interval
				LocalDateTime lastRunForPlayer = this.playerStore.getIntervalRecordForPlayer(player.getName(), intervalName);
				long diff = 0;
				if ( lastRunForPlayer != null ) {
					Duration duration = Duration.between(LocalDateTime.now(), lastRunForPlayer);					
					diff = Math.abs(duration.toMinutes());
				}

				if (
					// Never run before; run it now
					lastRunForPlayer == null ||
					// Player's last run of this interval is more minutes than the interval minutes
					diff >= this.intervalStore.getIntervalMinutes(intervalName)
				) {
					for (String cmd : this.intervalStore.getIntervalCommands(intervalName)) {
						// Dispatch commands
						this.dispatchCommandSync(
							// Replace placeholders
							this.replaceStringPlaceholders(cmd, player)
						);
					}

					// Send the message to the player
					String msg = this.intervalStore.getIntervalUserMessage(intervalName);
					if (!ValidityHelper.isStringEmpty(msg)) {
						// this.logger.info(String.format("Interval '%s': Message sent to player '%s'", intervalName, player.getName()));
						player.sendMessage(this.replaceStringPlaceholders(
							this.intervalStore.getIntervalUserMessage(intervalName),
							player
						));
					}

					// Update the interval run datetime for this player
					this.logger.info(String.format("Invoking actions in interval '%s' for player '%s'", intervalName, player.getName()));
					this.playerStore.updateIntervalForPlayer(player.getName(), intervalName);

					// TODO: Check if saving to db each time a change occurs is not too expensive
					// Alternatives are either save only on disable (risky?)
					// Or have a secondary task that saves all data every 30 minutes
					this.savePlayerDataToDatabase(player.getName());
				}
			}
		}
	}
	
	private void loadDataFromDatabase() {
		// TODO: Potential optimization: only load initial data of online users
		// So we can potentially only keep relevant players in memory, rather
		// than all players.
		ArrayList<DatabaseItem> dataItems = this.database.getAllPlayerRecords();
		if (dataItems == null || dataItems.isEmpty()) {
			this.logger.info(String.format("Initial database is empty. Starting from player data."));
			return;
		}

		int counter = 0;
		for (DatabaseItem item : dataItems) {
			this.loadPlayerDataFromDatabase(item);
			counter++;
		}
		this.logger.info(String.format("Loaded data from database for %d players.", counter));
	}
	
	private void loadPlayerDataFromDatabase(DatabaseItem dataItem) {
		if (dataItem == null) {
			return;
		}
		this.playerStore.addPlayer(dataItem.getPlayer());
		this.playerStore.setIntervalForPlayer(dataItem.getPlayer(), dataItem.getInterval(), dataItem.getLastRun());
	}
	
	private void saveAllDataToDatabase() {
		this.logger.info("Saving data to the database.");
		// TODO: This will be better if batched...
		for (String playerName : this.playerStore.getPlayerNames()) {
			this.logger.info(String.format("Saved player data in the database for %s", playerName));
			this.savePlayerDataToDatabase(playerName);
		}
	}
	
	private void savePlayerDataToDatabase(String playerName) {
		for (String intervalName : this.intervalStore.getIntervalNames()) {
			LocalDateTime lastRun = this.playerStore.getIntervalRecordForPlayer(playerName, intervalName);
			if (lastRun == null) {
				// No info yet for this interval; no need to save it
				continue;
			}
			this.database.savePlayerRecord(
				playerName,
				intervalName,
				lastRun.format(LDT_FORMATTER)
			);
		}
	}

	private void startIntervalTask() {
    	this.intervalTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
    		this.processAllIntervals();
    	}, 20L * 60, 20L * 30); // Every 1 minute; wait 30 seconds to start
    }

    /**
	 * Dispatches a command synchronously
	 *
	 * @param cmd The command to dispatch
	 */
	private void dispatchCommandSync(final String cmd) {
		final Server server = this.plugin.getServer();
		
		this.plugin.getServer().getScheduler().runTask(this.plugin, () -> server.dispatchCommand(server.getConsoleSender(), cmd));
	}

	private String replaceStringPlaceholders(String str, Player player) {
		String replacer = "(?i)%player%"; // Replace case insensitive
		return str.replaceAll(replacer, player.getName());
	}
}

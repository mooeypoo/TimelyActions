package io.github.mooeypoo.timelyactions.managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import io.github.mooeypoo.timelyactions.TimelyActions;
import io.github.mooeypoo.timelyactions.config.ConfigHandler;
import io.github.mooeypoo.timelyactions.config.interfaces.IntervalSectionConfigInterface;
import io.github.mooeypoo.timelyactions.config.interfaces.MainConfigInterface;
import io.github.mooeypoo.timelyactions.store.IntervalStore;
import io.github.mooeypoo.timelyactions.store.PlayerStore;

public class ProcessManager {
	private TimelyActions plugin;
	private ConfigHandler config;
	private IntervalStore intervalStore = new IntervalStore();
	private PlayerStore playerStore = new PlayerStore();

	public ProcessManager(TimelyActions plugin) {
		this.plugin = plugin;
		this.config = new ConfigHandler(this.plugin);
	}

	public void initialize() {
		// Read the config
		try {
			config.reload();
		} catch (Exception e) {
			this.plugin.getLogger().warning("Error reading configuration: " + e.getMessage());
			this.plugin.getLogger().warning("TimelyActions did not load properly. Please review your configuration files and restart the plugin.");
			return;
		}
		// TODO: Read the DB for initial values

		// Build the interval store from the config
		MainConfigInterface configData = null;
		try {
			configData = this.config.getData();
		} catch (Exception e) {
			this.plugin.getLogger().warning("Problem reading the main config for TimelyActions. Plugin will not work properly; please check your config files and reload the plugin.");
			return;
		}
		
		for(String intervalName : configData.intervals().keySet()) {
			IntervalSectionConfigInterface intervalData = configData.intervals().get(intervalName);

			// Make sure the data is valid
			if (intervalData.every_minutes() <= 0 || intervalData.commands().isEmpty()) {
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

		// Build the player store from online players
		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			this.playerStore.addPlayer(player);
			
			// Update interval values
			for (String intervalName : this.intervalStore.getIntervalNames()) {
				// TODO: Check that the user has permissions?
				// Not sure about this yet... since users can be *given*
				// permission mid-process, so we will need to check whether
				// users have permissions when we check the interval times
				// anyways.

				// TODO: Check the DB for existing values for this player for this interval
				
				// If no existing value, skip; the value will be added
				// when the system does a first-run for the interval
				// time checks
			}
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
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Make sure player is in store (no-op if the player already exists)
			// This is sanity-check; the players should've been added and removed
			// on join and leave
			this.playerStore.addPlayer(player);
			
			// Check all intervals
			for(String intervalName : this.intervalStore.getIntervalNames()) {
				// Check that the user has matching permissions
				if (!this.intervalStore.doesPlayerHaveIntervalPermission(player, intervalName)) {
					// User does not have permission
					continue;
				}
				
				// Check if the user's last-run is outside the interval
				LocalDateTime lastRunForPlayer = this.playerStore.getIntervalRecordForPlayer(player, intervalName);
				long diff = 0;
				if ( lastRunForPlayer != null ) {
					Duration duration = Duration.between(LocalDateTime.now(), lastRunForPlayer);					
					diff = Math.abs(duration.toMinutes());
				}

				if (
					// Never run before; run it now
					lastRunForPlayer == null ||
					// Player's last run of this interval is more minutes than the interval minutes
					diff > this.intervalStore.getIntervalMinutes(intervalName)
				) {
					for ( String cmd : this.intervalStore.getIntervalCommands(intervalName)) {
						// Dispatch commands
						this.dispatchCommandSync(
							// Replace placeholders
							this.replaceStringPlaceholders(cmd, player)
						);
					}

					// Send the message to the player
					if (this.intervalStore.getIntervalUserMessage(intervalName) != null) {
						this.plugin.getLogger().info(String.format("Interval '%s': Message sent to player '%s'", intervalName, player.getName()));

						player.sendMessage(this.replaceStringPlaceholders(
							this.intervalStore.getIntervalUserMessage(intervalName),
							player
						));
					}

					// Update the interval run datetime for this player
					this.plugin.getLogger().info(String.format("Invoking actions in interval '%s' for player '%s'", intervalName, player.getName()));
					this.playerStore.updateIntervalForPlayer(player, intervalName);
				}
			}
		}
	}

	public void stop() {
		// TODO: Save everything to the DB
	}

    /**
	 * Dispatches a command as sync.
	 *
	 * @param cmd The command to dispatch sync.
	 */
	private void dispatchCommandSync(final String cmd) {
		final Server server = this.plugin.getServer();
		
		this.plugin.getServer().getScheduler().runTask(this.plugin, () -> server.dispatchCommand(server.getConsoleSender(), cmd));
	}

	private String replaceStringPlaceholders(String str, Player player) {
		String replacer = "(?i)%player%"; // Replace case insensitive
		return str.replaceAll(replacer, player.getName());
	}

	// TODO: Manage player joining
	// TODO: Manage player leaving
}

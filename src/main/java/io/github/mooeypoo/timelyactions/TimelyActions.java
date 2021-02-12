package io.github.mooeypoo.timelyactions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.timelyactions.command.TimelyActionsCommandExecutor;
import io.github.mooeypoo.timelyactions.managers.ProcessManager;
import io.github.mooeypoo.timelyactions.utils.TimelyLogger;

public class TimelyActions extends JavaPlugin implements Listener {
	private ProcessManager processManager;
	
	@Override
	public void onEnable() {
		TimelyLogger logger = TimelyLogger.getInstance();
		logger.setPluginLogger(this.getLogger());

		this.getLogger().info("Initializing TimelyActions config and data...");
		this.processManager = new ProcessManager(this);
		
		// Connect events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, (this));
		
		// Start processing
		this.getLogger().info("Starting timed interval actions...");
		this.processManager.initialize();

		// Initialize command
		this.getCommand("timelyactions").setExecutor(new TimelyActionsCommandExecutor(this));

		// TODO: Add commands to reload config
		// TODO: Add command to stop and restart the task operation

		this.getLogger().info("TimelyActions is enabled.");
	}

    @Override
	public void onDisable() {
    	this.processManager.stop();

    	this.getLogger().info("Disabling TimelyActions...");
    }

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.processManager.addPlayer(event.getPlayer().getName());
	}
	
	public ProcessManager getProcessManager() {
		return this.processManager;
	}
}

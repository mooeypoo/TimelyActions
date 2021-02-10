package io.github.mooeypoo.timelyactions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import io.github.mooeypoo.timelyactions.managers.ProcessManager;

public class TimelyActions extends JavaPlugin implements Listener {
	private ProcessManager processManager;
	private BukkitTask intervalTask;
	
	@Override
	public void onEnable() {
		this.getLogger().info("Enabling TimelyActions...");

		this.getLogger().info("Initializing TimelyActions config and data...");
		this.processManager = new ProcessManager(this);
		this.processManager.initialize();
		
		// Connect events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, (this));
		
		// Start task
		this.startIntervalTask();

		this.getLogger().info("TimelyActions is enabled.");
	}

    @Override
	public void onDisable() {
    	this.intervalTask.cancel();
    	this.processManager.stop();

    	this.getLogger().info("Disabling TimelyActions...");
    }


	private void startIntervalTask() {
    	this.intervalTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
    		this.processManager.processAllIntervals();
    	}, 20L * 60, 20L * 30); // Every 1 minute; wait 30 seconds to start
    }
}

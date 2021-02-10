package io.github.mooeypoo.timelyactions.config;

import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.timelyactions.config.interfaces.MainConfigInterface;

public class ConfigHandler {
	private ConfigLoader<MainConfigInterface> config;
	private JavaPlugin plugin;

	public ConfigHandler(JavaPlugin plugin) {
		this.plugin = plugin;
		// Read config
		// TODO: Enable reloading of the config
		this.config = ConfigLoader.create(
			Paths.get(this.plugin.getDataFolder().getPath()),
			"config.yml",
			MainConfigInterface.class
		);
	}
	
	public void reload() throws Exception {
		this.config.reloadConfig();
	}
	
	public MainConfigInterface getData() throws Exception {
		if (this.config == null) {
			return null;
		}
		return this.config.getConfigData();
	}
}

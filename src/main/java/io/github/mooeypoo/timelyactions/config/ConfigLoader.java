package io.github.mooeypoo.timelyactions.config;

import java.io.IOException;
import java.nio.file.Path;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

public class ConfigLoader<C> extends ConfigurationHelper<C> {
	private C configData;

	private ConfigLoader(Path configFolder, String fileName, ConfigurationFactory<C> factory) {
		super(configFolder, fileName, factory);
	}

	public static <C> ConfigLoader<C> create(Path configFolder, String fileName, Class<C> configClass) {
		// SnakeYaml example
		SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
//				.useCommentingWriter(true) // Enables writing YAML comments
				.build();
		return new ConfigLoader<>(configFolder, fileName,
				SnakeYamlConfigurationFactory.create(configClass, ConfigurationOptions.defaults(), yamlOptions));
	}

	public synchronized void reloadConfig() throws Exception {
		try {
			configData = reloadConfigData();
		} catch (IOException ex) {
			throw new Exception(
				"The was a problem loading the config file."
			);
		} catch (ConfigFormatSyntaxException ex) {
			configData = getFactory().loadDefaults();
			throw new Exception(
				"The yaml syntax of the config is malformed. Using defaults, instead."
			);
		} catch (InvalidConfigException ex) {
			configData = getFactory().loadDefaults();
			throw new Exception(
				"The keys and values used in this config file are malformed. Using defaults, instead."
			);
		}
	}

	public synchronized C getConfigData() throws Exception {
		if (configData == null) {
			throw new Exception(
				"Configuration file was not yet loaded."
			);
		}
		return configData;
	}
}

package de.flo56958.minetinker.utils;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.modifiers.Modifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ConfigurationManager {
	/*
	 * Stores all config-files with their name
	 */
	private static final HashMap<String, FileConfiguration> configs = new HashMap<>();
	private static final HashMap<FileConfiguration, File> configsFolder = new HashMap<>();

	private ConfigurationManager() {} //So nobody can instantiate this class

	/**
	 * Gets the specified config file
	 *
	 * @param modifier The Name of the file (Enum modifiers_Config)
	 * @return The FileConfiguration with the given name
	 */
	public static FileConfiguration getConfig(Modifier modifier) {
		return configs.get(modifier.getFileName());
	}

	/**
	 * Gets the specified config file
	 *
	 * @param file The Name of the file
	 * @return The FileConfiguration with the given name
	 */
	public static FileConfiguration getConfig(String file) {
		return configs.get(file);
	}

	public static void reload() {
		loadConfig("", "layout.yml");

		loadConfig("", "Elytra.yml");

		loadConfig("", "Modifiers.yml");

		//importing Main configuration into system
		configs.put("config.yml", MineTinker.getPlugin().getConfig());
		configsFolder.put(MineTinker.getPlugin().getConfig(), new File(MineTinker.getPlugin().getDataFolder(), "config.yml"));
	}

	/**
	 * creates a config file in the specified folder
	 *
	 * @param folder The name of the folder
	 * @param file   The name of the file
	 */
	public static void loadConfig(String folder, String file) {
		File customConfigFile = new File(MineTinker.getPlugin().getDataFolder(), folder + file);
		FileConfiguration fileConfiguration = configs.getOrDefault(file, new YamlConfiguration());

		configsFolder.put(fileConfiguration, customConfigFile);
		configs.put(file, fileConfiguration);

		if (!customConfigFile.exists()) return;

		try {
			fileConfiguration.load(customConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void saveConfig(@NotNull FileConfiguration config) {
		try {
			config.save(configsFolder.get(config));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Contract(pure = true)
	public static @NotNull Set<String> getAllConfigNames() {
		return configs.keySet();
	}
}

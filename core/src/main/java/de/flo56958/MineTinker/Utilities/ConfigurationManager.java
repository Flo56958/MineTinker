package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.ModManager;
import de.flo56958.MineTinker.Modifiers.Modifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
	private ConfigurationManager() {
	}

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
		//clean up before reload
		configs.clear();
		configsFolder.clear();

		loadConfig("", "layout.yml");

		loadConfig("", "BuildersWand.yml");

		loadConfig("", "Elytra.yml");

		loadConfig("", "Modifiers.yml");

		for (Modifier modifier : ModManager.instance().getAllMods()) {
			if (modifier.getFileName().isEmpty()) {
				continue;
			}

			loadConfig("Modifiers" + File.separator, modifier.getFileName());
		}

		//importing Main configuration into system
		configs.put("config.yml", Main.getPlugin().getConfig());
		configsFolder.put(Main.getPlugin().getConfig(), new File(Main.getPlugin().getDataFolder(), "config.yml"));
	}

	/**
	 * creates a config file in the specified folder
	 *
	 * @param folder The name of the folder
	 * @param file   The name of the file
	 */
	public static void loadConfig(String folder, String file) {
		File customConfigFile = new File(Main.getPlugin().getDataFolder(), folder + file);
		YamlConfiguration fileConfiguration = new YamlConfiguration();

		configsFolder.put(fileConfiguration, customConfigFile);
		configs.put(file, fileConfiguration);

		if (customConfigFile.exists()) {
			try {
				fileConfiguration.load(customConfigFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveConfig(FileConfiguration config) {
		try {
			config.save(configsFolder.get(config));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<String> getAllConfigNames() {
		return configs.keySet();
	}
}

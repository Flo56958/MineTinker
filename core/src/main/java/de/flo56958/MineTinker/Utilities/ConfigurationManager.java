package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Types.ModifierType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigurationManager {
	private ConfigurationManager() {}
	
    /*
     * Stores all config-files with their name
     */
    private static final HashMap<String, FileConfiguration> configs = new HashMap<>();
    private static final HashMap<FileConfiguration, File> configsFolder = new HashMap<>();
    
    /**
     * Gets the specified config file
     * @param modifier The Name of the file (Enum modifiers_Config)
     * @return The FileConfiguration with the given name
     */
    public static FileConfiguration getConfig(ModifierType modifier) {
        return configs.get(modifier.getFileName());
    }

    /**
     * Gets the specified config file
     * @param file The Name of the file
     * @return The FileConfiguration with the given name
     */
    public static FileConfiguration getConfig(String file) {
        return configs.get(file);
    }

    public static void reload() {
        for (ModifierType modifier : ModifierType.values()) {
        	loadConfig("Modifiers" + File.separator, modifier.getFileName());
        }

        loadConfig("", "layout.yml");

        loadConfig("", "BuildersWand.yml");

        loadConfig("", "Elytra.yml");
        loadConfig("", "Trident.yml");
    }

    /**
     * creates a config file in the specifid folder
     * @param folder The name of the folder
     * @param file The name of the file
     */
    public static void loadConfig(String folder, String file) {
        File customConfigFile = new File(Main.getPlugin().getDataFolder(), folder + file);
        YamlConfiguration fileConfiguration = new YamlConfiguration();

        configsFolder.put(fileConfiguration, customConfigFile);
        configs.put(file, fileConfiguration);
        
        if (customConfigFile.exists()) {
        	try {
	            fileConfiguration.load(customConfigFile);
	        } catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
        }
    }

    //TODO: Make Set-first-Time option
    public static void saveConfig(FileConfiguration config) {
    	try {
			config.save(configsFolder.get(config));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

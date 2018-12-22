package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigurationManager {
    private final Main main;
    /**
     * Stores all config-files with their name
     */
    private final HashMap<String, FileConfiguration> configs = new HashMap<>();
    
    /**
     * Class constructor
     * @param main The main class-instance
     */
    public ConfigurationManager(Main main) {
        this.main = main;
       reload();
    }
    
    /**
     * Gets the specified config file
     * @param file The Name of the file (Enum modifiers_Config)
     * @return The FileConfiguration with the given name
     */
    public FileConfiguration getConfig(Modifiers_Config modifier) {
        return configs.get(modifier.toString());
    }

    /**
     * Gets the specified config file
     * @param file The Name of the file
     * @return The FileConfiguration with the given name
     */
    public FileConfiguration getConfig(String file) {
        return configs.get(file);
    }

    public void reload() {
        for (Modifiers_Config modifier : Modifiers_Config.values()) {
            createConfig("Modifiers" + File.separator, modifier.toString());
        }

        createConfig("", "BuildersWand.yml");
        createConfig("", "Elevator.yml");
    }

    /**
     * creates a config file in the specifid folder
     * @param folder The name of the folder
     * @param file The name of the file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createConfig(String folder, String file) {
        File customConfigFile = new File(main.getDataFolder(), folder + file);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            main.saveResource(folder + file, false);
        }

        YamlConfiguration fileConfiguration = new YamlConfiguration();
        
        try {
            fileConfiguration.load(customConfigFile);
            configs.put(file, fileConfiguration);
        } catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }
}

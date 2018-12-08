package de.flo56958.MineTinker.Utilities;

import de.flo56958.MineTinker.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    private Main main;
    private HashMap<String, FileConfiguration> configs = new HashMap<>();

    public Config(Main main) {
        this.main = main;

        String[] modifiers = { "Auto-Smelt.yml", "Beheading.yml", "Directing.yml", "Ender.yml", "Experienced.yml", "Fiery.yml",
                "Glowing.yml", "Haste.yml", "Infinity.yml", "Knockback.yml", "Light-Weight.yml", "Luck.yml", "Melting.yml",
                "Poisonous.yml", "Power.yml", "Protecting.yml", "Reinforced.yml", "Self-Repair.yml", "Sharpness.yml",
                "Shulking.yml", "Silk-Touch.yml", "Sweeping.yml", "Timber.yml", "Webbed.yml", "Extra-Modifier.yml" };

        for (String modifier : modifiers) createConfig("Modifiers" + File.separator, modifier);

        createConfig("", "BuildersWand.yml");
        createConfig("", "Elevator.yml");
    }

    public FileConfiguration getConfig(String file) {
        return configs.get(file);
    }

    private void createConfig(String folder, String file) {
        File customConfigFile = new File(main.getDataFolder(), folder + file);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            main.saveResource(folder + file, false);
        }

        FileConfiguration fileConfiguration = new YamlConfiguration();

        try {
            fileConfiguration.load(customConfigFile);
            configs.put(file, fileConfiguration);
        } catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }
}

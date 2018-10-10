package de.flo56958.MineTinker.Data;

import org.bukkit.configuration.file.FileConfiguration;

import de.flo56958.MineTinker.Main;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static final List<String> SWORDS = config.getStringList("AllowedTools.Swords");
    public static final List<String> AXES = config.getStringList("AllowedTools.Axes");
    public static final List<String> PICKAXES = config.getStringList("AllowedTools.Pickaxes");
    public static final List<String> SHOVELS = config.getStringList("AllowedTools.Shovels");
    public static final List<String> HOES = config.getStringList("AllowedTools.Hoes");
    public static final List<String> BOWS = config.getStringList("AllowedTools.Bows");
    public static final List<String> MISC = config.getStringList("AllowedTools.Misc");

    public static final List<String> WORLDS = config.getStringList("AllowedWorlds");
    public static final List<String> WORLDS_ELEVATOR = config.getStringList("Elevator.AllowedWorlds");
    public static final List<String> WORLDS_BUILDERSWANDS = config.getStringList("Builderswands.AllowedWorlds");
    public static final List<String> WORLDS_SPAWNERS = config.getStringList("Spawner.AllowedWorlds");

    public static ArrayList<String> getAllowedModifiers(){
        ArrayList<String> allowed = new ArrayList<>();
        if (config.getBoolean("Modifiers.Auto-Smelt.allowed")) {
            allowed.add(config.getString("Modifiers.Auto-Smelt.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Beheading.allowed")) {
            allowed.add(config.getString("Modifiers.Beheading.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Directing.allowed")) {
            allowed.add(config.getString("Modifiers.Directing.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Ender.allowed")) {
            allowed.add(config.getString("Modifiers.Ender.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Extra-Modifier.allowed")) {
            allowed.add(config.getString("Modifiers.Extra-Modifier.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Fiery.allowed")) {
            allowed.add(config.getString("Modifiers.Fiery.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Glowing.allowed")) {
            allowed.add(config.getString("Modifiers.Glowing.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Haste.allowed")) {
            allowed.add(config.getString("Modifiers.Haste.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Knockback.allowed")) {
            allowed.add(config.getString("Modifiers.Knockback.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Infinity.allowed")) {
            allowed.add(config.getString("Modifiers.Infinity.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Melting.allowed")) {
            allowed.add(config.getString("Modifiers.Melting.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Luck.allowed")) {
            allowed.add(config.getString("Modifiers.Luck.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Poisonous.allowed")) {
            allowed.add(config.getString("Modifiers.Poisonous.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Power.allowed")) {
            allowed.add(config.getString("Modifiers.Power.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Reinforced.allowed")) {
            allowed.add(config.getString("Modifiers.Reinforced.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Self-Repair.allowed")) {
            allowed.add(config.getString("Modifiers.Self-Repair.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Sharpness.allowed")) {
            allowed.add(config.getString("Modifiers.Sharpness.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Shulking.allowed")) {
            allowed.add(config.getString("Modifiers.Shulking.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Silk-Touch.allowed")) {
            allowed.add(config.getString("Modifiers.Silk-Touch.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Sweeping.allowed")) {
            allowed.add(config.getString("Modifiers.Sweeping.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Timber.allowed")) {
            allowed.add(config.getString("Modifiers.Timber.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.Webbed.allowed")) {
            allowed.add(config.getString("Modifiers.Webbed.name").toLowerCase());
        }
        if (config.getBoolean("Modifiers.XP.allowed")) {
            allowed.add(config.getString("Modifiers.XP.name").toLowerCase());
        }
        return allowed;
    }

    public static final List<String> DROPLOOT = config.getStringList("LevelUpEvents.DropLoot.Items");

}

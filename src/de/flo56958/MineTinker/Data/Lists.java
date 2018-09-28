package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Lists {

    public static final List<String> SWORDS = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Swords");
    public static final List<String> AXES = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Axes");
    public static final List<String> PICKAXES = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Pickaxes");
    public static final List<String> SHOVELS = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Shovels");
    public static final List<String> HOES = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Hoes");
    public static final List<String> BOWS = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Bows");
    public static final List<String> MISC = (List<String>) Main.getPlugin().getConfig().getList("AllowedTools.Misc");

    public static final List<String> WORLDS = (List<String>) Main.getPlugin().getConfig().getList("AllowedWorlds");
    public static final List<String> WORLDS_ELEVATOR = (List<String>) Main.getPlugin().getConfig().getList("Elevator.AllowedWorlds");
    public static final List<String> WORLDS_BUILDERSWANDS = (List<String>) Main.getPlugin().getConfig().getList("Builderswands.AllowedWorlds");

    public static List<String> getAllowedModifiers(){
        List<String> allowed = new ArrayList<>();
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Auto-Smelt.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Beheading.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Ender.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Ender.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Extra-Modifier.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Fiery.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Glowing.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Glowing.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Haste.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Knockback.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Knockback.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Infinity.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Infinity.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Luck.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Poisonous.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Poisonous.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Power.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Reinforced.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Self-Repair.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Sharpness.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Shulking.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Shulking.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Silk-Touch.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sweeping.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.Sweeping.name").toLowerCase());
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
            allowed.add(Main.getPlugin().getConfig().getString("Modifiers.XP.name").toLowerCase());
        }
        return allowed;
    }

    public static final List<String> DROPLOOT = (List<String>) Main.getPlugin().getConfig().getList("LevelUpEvents.DropLoot.Items");
}

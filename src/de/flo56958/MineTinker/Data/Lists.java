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

    public static final List<String> WORLDS = (List<String>) Main.getPlugin().getConfig().getList("AllowedWorlds");
    public static final List<String> WORLDS_ELEVATOR = (List<String>) Main.getPlugin().getConfig().getList("Elevator.AllowedWorlds");
    public static final List<String> WORLDS_BUILDERSWANDS = (List<String>) Main.getPlugin().getConfig().getList("Builderswands.AllowedWorlds");

    public static List<String> getAllowedModifiers(){
        List<String> allowed = new ArrayList<>();
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Auto-Smelt.allowed")) {
            allowed.add("Auto-Smelt");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Beheading.allowed")) {
            allowed.add("Beheading");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Extra-Modifier.allowed")) {
            allowed.add("Extra-Modifier");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Fiery.allowed")) {
            allowed.add("Fiery");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Haste.allowed")) {
            allowed.add("Haste");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Power.allowed")) {
            allowed.add("Power");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Reinforced.allowed")) {
            allowed.add("Reinforced");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Sharpness.allowed")) {
            allowed.add("Sharpness");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Luck.allowed")) {
            allowed.add("Luck");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Silk-Touch.allowed")) {
            allowed.add("Silk-Touch");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
            allowed.add("Self-Repair");
        }
        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
            allowed.add("XP");
        }
        return allowed;
    }

    public static final List<String> DROPLOOT = (List<String>) Main.getPlugin().getConfig().getList("LevelUpEvents.DropLoot.Items");
}

package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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

    public static ArrayList<ItemStack> getModifierItems() {
        ArrayList<ItemStack> modifiers = new ArrayList<>();
        modifiers.add(Modifiers.AUTOSMELT_MODIFIER);
        modifiers.add(Modifiers.BEHEADING_MODIFIER);
        modifiers.add(Modifiers.DIRECTING_MODIFIER);
        modifiers.add(Modifiers.ENDER_MODIFIER);
        modifiers.add(Modifiers.FIERY_MODIFIER);
        modifiers.add(Modifiers.GLOWING_MODIFIER);
        modifiers.add(Modifiers.HASTE_MODIFIER);
        modifiers.add(Modifiers.INFINITY_MODIFIER);
        modifiers.add(Modifiers.KNOCKBACK_MODIFIER);
        modifiers.add(Modifiers.LUCK_MODIFIER);
        modifiers.add(Modifiers.MELTING_MODIFIER);
        modifiers.add(Modifiers.POISONOUS_MODIFIER);
        modifiers.add(Modifiers.POWER_MODIFIER);
        modifiers.add(Modifiers.REINFORCED_MODIFIER);
        modifiers.add(Modifiers.SELFREPAIR_MODIFIER);
        modifiers.add(Modifiers.SHARPNESS_MODIFIER);
        modifiers.add(Modifiers.SHULKING_MODIFIER);
        modifiers.add(Modifiers.SILKTOUCH_MODIFIER);
        modifiers.add(Modifiers.SWEEPING_MODIFIER);
        modifiers.add(Modifiers.TIMBER_MODIFIER);
        modifiers.add(Modifiers.WEBBED_MODIFIER);
        return modifiers;
    }

    public static ArrayList<Material> getWoodLeaves() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.ACACIA_LEAVES);
        mats.add(Material.BIRCH_LEAVES);
        mats.add(Material.DARK_OAK_LEAVES);
        mats.add(Material.JUNGLE_LEAVES);
        mats.add(Material.OAK_LEAVES);
        mats.add(Material.SPRUCE_LEAVES);
        return mats;
    }

    public static ArrayList<Material> getWoodPlanks() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.ACACIA_PLANKS);
        mats.add(Material.BIRCH_PLANKS);
        mats.add(Material.DARK_OAK_PLANKS);
        mats.add(Material.JUNGLE_PLANKS);
        mats.add(Material.OAK_PLANKS);
        mats.add(Material.SPRUCE_PLANKS);
        return mats;
    }

    public static ArrayList<Material> getWoodLogs() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.ACACIA_LOG);
        mats.add(Material.BIRCH_LOG);
        mats.add(Material.DARK_OAK_LOG);
        mats.add(Material.JUNGLE_LOG);
        mats.add(Material.OAK_LOG);
        mats.add(Material.SPRUCE_LOG);
        return mats;
    }

    public static ArrayList<Material> getWoodStrippedLogs() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.STRIPPED_ACACIA_LOG);
        mats.add(Material.STRIPPED_BIRCH_LOG);
        mats.add(Material.STRIPPED_DARK_OAK_LOG);
        mats.add(Material.STRIPPED_JUNGLE_LOG);
        mats.add(Material.STRIPPED_OAK_LOG);
        mats.add(Material.STRIPPED_SPRUCE_LOG);
        return mats;
    }

    public static ArrayList<Material> getWoodWood() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.ACACIA_WOOD);
        mats.add(Material.BIRCH_WOOD);
        mats.add(Material.DARK_OAK_WOOD);
        mats.add(Material.JUNGLE_WOOD);
        mats.add(Material.OAK_WOOD);
        mats.add(Material.SPRUCE_WOOD);
        return mats;
    }

    public static ArrayList<Material> getWoodStrippedWood() {
        ArrayList<Material> mats = new ArrayList<>();
        mats.add(Material.STRIPPED_ACACIA_WOOD);
        mats.add(Material.STRIPPED_BIRCH_WOOD);
        mats.add(Material.STRIPPED_DARK_OAK_WOOD);
        mats.add(Material.STRIPPED_JUNGLE_WOOD);
        mats.add(Material.STRIPPED_OAK_WOOD);
        mats.add(Material.STRIPPED_SPRUCE_WOOD);
        return mats;
    }

    public static final List<String> DROPLOOT = config.getStringList("LevelUpEvents.DropLoot.Items");

}

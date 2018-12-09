package de.flo56958.MineTinker.Data;

import de.flo56958.MineTinker.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    private static final FileConfiguration config = Main.getPlugin().getConfig();

    public static final List<String> WORLDS = config.getStringList("AllowedWorlds");
    public static final List<String> WORLDS_ELEVATOR = Main.getMain().getConfigurations().getConfig("Elevator.yml").getStringList("Elevator.AllowedWorlds");
    public static final List<String> WORLDS_BUILDERSWANDS = Main.getMain().getConfigurations().getConfig("BuildersWand.yml").getStringList("BuildersWand.AllowedWorlds");
    public static final List<String> WORLDS_SPAWNERS = config.getStringList("Spawners.AllowedWorlds");
    public static final List<String> WORLDS_EASYHARVEST = config.getStringList("EasyHarvest.AllowedWorlds");

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

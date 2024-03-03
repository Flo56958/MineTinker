package de.flo56958.minetinker.data;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Lists {

	public static final ConcurrentHashMap<Player, BlockFace> BLOCKFACE = new ConcurrentHashMap<>();
	private static final FileConfiguration config = MineTinker.getPlugin().getConfig();
	public static final List<String> DROPLOOT = config.getStringList("LevelUpEvents.DropLoot.Items");
	public static List<String> WORLDS;
	public static List<String> WORLDS_BUILDERSWANDS;
	public static List<String> WORLDS_EASYHARVEST;
	public static List<String> NAME_COMMAND_BLACKLIST;

	static {
		reload();
	}

	public static void reload() {
		WORLDS = config.getStringList("BannedWorlds");
		WORLDS_BUILDERSWANDS = ConfigurationManager.getConfig("BuildersWand.yml").getStringList("BannedWorlds");
		WORLDS_EASYHARVEST = config.getStringList("EasyHarvest.BannedWorlds");
		NAME_COMMAND_BLACKLIST = config.getStringList("NameCommandBlacklist");
	}

	public static ArrayList<Material> getLeatherArmor() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.LEATHER_CHESTPLATE);
		mats.add(Material.LEATHER_HELMET);
		mats.add(Material.LEATHER_BOOTS);
		mats.add(Material.LEATHER_LEGGINGS);

		return mats;
	}

	/**
	 * @return All Leaf Type
	 */
	public static ArrayList<Material> getWoodLeaves() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.ACACIA_LEAVES);
		mats.add(Material.BIRCH_LEAVES);
		mats.add(Material.DARK_OAK_LEAVES);
		mats.add(Material.JUNGLE_LEAVES);
		mats.add(Material.OAK_LEAVES);
		mats.add(Material.SPRUCE_LEAVES);
		mats.add(Material.NETHER_WART_BLOCK);
		mats.add(Material.WARPED_WART_BLOCK);
		mats.add(Material.AZALEA_LEAVES);

		if (MineTinker.is19compatible)
			mats.add(Material.MANGROVE_LEAVES);
		if (MineTinker.is20compatible)
			mats.add(Material.CHERRY_LEAVES);

		return mats;
	}

	/**
	 * @return All Plank types
	 */
	public static ArrayList<Material> getWoodPlanks() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.ACACIA_PLANKS);
		mats.add(Material.BIRCH_PLANKS);
		mats.add(Material.DARK_OAK_PLANKS);
		mats.add(Material.JUNGLE_PLANKS);
		mats.add(Material.OAK_PLANKS);
		mats.add(Material.SPRUCE_PLANKS);
		mats.add(Material.CRIMSON_PLANKS);
		mats.add(Material.WARPED_PLANKS);

		if (MineTinker.is19compatible)
			mats.add(Material.MANGROVE_PLANKS);
		if (MineTinker.is20compatible)
			mats.add(Material.CHERRY_PLANKS);
		return mats;
	}

	/**
	 * @return All Log types
	 */
	public static ArrayList<Material> getWoodLogs() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.ACACIA_LOG);
		mats.add(Material.BIRCH_LOG);
		mats.add(Material.DARK_OAK_LOG);
		mats.add(Material.JUNGLE_LOG);
		mats.add(Material.OAK_LOG);
		mats.add(Material.SPRUCE_LOG);
		mats.add(Material.CRIMSON_STEM);
		mats.add(Material.WARPED_STEM);

		if (MineTinker.is19compatible) {
			mats.add(Material.MANGROVE_LOG);

			// Roots are considered part of the tree
			mats.add(Material.MANGROVE_ROOTS);
			mats.add(Material.MUDDY_MANGROVE_ROOTS);
		}
		if (MineTinker.is20compatible)
			mats.add(Material.CHERRY_LOG);
		return mats;
	}

	/**
	 * @return All stripped Log types
	 */
	public static ArrayList<Material> getWoodStrippedLogs() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.STRIPPED_ACACIA_LOG);
		mats.add(Material.STRIPPED_BIRCH_LOG);
		mats.add(Material.STRIPPED_DARK_OAK_LOG);
		mats.add(Material.STRIPPED_JUNGLE_LOG);
		mats.add(Material.STRIPPED_OAK_LOG);
		mats.add(Material.STRIPPED_SPRUCE_LOG);
		mats.add(Material.STRIPPED_CRIMSON_STEM);
		mats.add(Material.STRIPPED_WARPED_STEM);

		if (MineTinker.is19compatible)
			mats.add(Material.STRIPPED_MANGROVE_LOG);
		if (MineTinker.is20compatible)
			mats.add(Material.STRIPPED_CHERRY_LOG);
		return mats;
	}

	/**
	 * @return All Wood types
	 */
	public static ArrayList<Material> getWoodWood() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.ACACIA_WOOD);
		mats.add(Material.BIRCH_WOOD);
		mats.add(Material.DARK_OAK_WOOD);
		mats.add(Material.JUNGLE_WOOD);
		mats.add(Material.OAK_WOOD);
		mats.add(Material.SPRUCE_WOOD);
		mats.add(Material.CRIMSON_HYPHAE);
		mats.add(Material.WARPED_HYPHAE);

		if (MineTinker.is19compatible)
			mats.add(Material.MANGROVE_WOOD);
		if (MineTinker.is20compatible)
			mats.add(Material.CHERRY_WOOD);
		return mats;
	}

	/**
	 * @return All stripped Wood types
	 */
	public static ArrayList<Material> getWoodStrippedWood() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.STRIPPED_ACACIA_WOOD);
		mats.add(Material.STRIPPED_BIRCH_WOOD);
		mats.add(Material.STRIPPED_DARK_OAK_WOOD);
		mats.add(Material.STRIPPED_JUNGLE_WOOD);
		mats.add(Material.STRIPPED_OAK_WOOD);
		mats.add(Material.STRIPPED_SPRUCE_WOOD);
		mats.add(Material.STRIPPED_CRIMSON_HYPHAE);
		mats.add(Material.STRIPPED_WARPED_HYPHAE);

		if (MineTinker.is19compatible)
			mats.add(Material.STRIPPED_MANGROVE_WOOD);
		if (MineTinker.is20compatible)
			mats.add(Material.STRIPPED_CHERRY_WOOD);
		return mats;
	}

}

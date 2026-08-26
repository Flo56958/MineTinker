package de.flo56958.minetinker.data;

import de.flo56958.minetinker.MineTinker;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class Lists {

	// Bukkit event dispatch is single-threaded, so this is only ever touched from the main thread
	public static final HashMap<Player, BlockFace> BLOCKFACE = new HashMap<>();
	public static List<String> DROPLOOT;
	public static Set<String> WORLDS;
	public static List<String> NAME_COMMAND_BLACKLIST;
	public static Set<String> DISABLE_EXP_FROM_DAMAGE_CAUSE;
	public static Set<String> DISABLE_EXP_FROM_ENTITY;

	static {
		reload();
	}

	public static void reload() {
		final FileConfiguration config = MineTinker.getPlugin().getConfig();
		WORLDS = Set.copyOf(config.getStringList("BannedWorlds"));
		NAME_COMMAND_BLACKLIST = config.getStringList("NameCommandBlacklist");
		DROPLOOT = config.getStringList("LevelUpEvents.DropLoot.Items");
		DISABLE_EXP_FROM_DAMAGE_CAUSE = Set.copyOf(config.getStringList("DisableExpFromDamageCause"));
		DISABLE_EXP_FROM_ENTITY = Set.copyOf(config.getStringList("DisableExpFromEntity"));
	}

	public static List<Material> getLeatherArmor() {
		final ArrayList<Material> mats = new ArrayList<>();

		mats.add(Material.LEATHER_CHESTPLATE);
		mats.add(Material.LEATHER_HELMET);
		mats.add(Material.LEATHER_BOOTS);
		mats.add(Material.LEATHER_LEGGINGS);

		return mats;
	}
}

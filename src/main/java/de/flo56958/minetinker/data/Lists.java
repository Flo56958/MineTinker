package de.flo56958.minetinker.data;

import de.flo56958.minetinker.MineTinker;
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
	public static List<String> DROPLOOT;
	public static List<String> WORLDS;
	public static List<String> NAME_COMMAND_BLACKLIST;

	static {
		reload();
	}

	public static void reload() {
		final FileConfiguration config = MineTinker.getPlugin().getConfig();
		WORLDS = config.getStringList("BannedWorlds");
		NAME_COMMAND_BLACKLIST = config.getStringList("NameCommandBlacklist");
		DROPLOOT = config.getStringList("LevelUpEvents.DropLoot.Items");
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

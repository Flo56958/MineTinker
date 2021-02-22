package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Drilling extends Modifier implements Listener {

	public static final ConcurrentHashMap<Location, Integer> events = new ConcurrentHashMap<>();
	private static Drilling instance;
	private ArrayList<Material> blacklist;
	private boolean treatAsWhitelist;
	private boolean toggleable;

	private Drilling() {
		super(MineTinker.getPlugin());
		customModelData = 10_045;
	}

	public static Drilling instance() {
		synchronized (Drilling.class) {
			if (instance == null)
				instance = new Drilling();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Drilling";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("Toggleable", true);
		config.addDefault("MaxLevel", 4);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", " D ");
		config.addDefault("Recipe.Middle", "DGD");
		config.addDefault("Recipe.Bottom", "GHG");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("G", Material.GOLD_BLOCK.name());
		recipeMaterials.put("H", Material.HOPPER.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		List<String> blacklistTemp = new ArrayList<>();

		blacklistTemp.add(Material.AIR.name());
		blacklistTemp.add(Material.BEDROCK.name());
		blacklistTemp.add(Material.WATER.name());
		blacklistTemp.add(Material.BUBBLE_COLUMN.name());
		blacklistTemp.add(Material.LAVA.name());
		blacklistTemp.add(Material.END_PORTAL.name());
		blacklistTemp.add(Material.END_CRYSTAL.name());
		blacklistTemp.add(Material.END_PORTAL_FRAME.name());
		blacklistTemp.add(Material.NETHER_PORTAL.name());

		config.addDefault("Blacklist", blacklistTemp);
		config.addDefault("TreatAsWhitelist", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.HOPPER);

		this.toggleable = config.getBoolean("Toggleable", true);
		this.treatAsWhitelist = config.getBoolean("TreatAsWhitelist", false);

		blacklist = new ArrayList<>();

		List<String> blacklistConfig = config.getStringList("Blacklist");

		for (String mat : blacklistConfig) {
			try {
				Material material = Material.valueOf(mat);

				if (blacklist == null) {
					continue;
				}

				blacklist.add(material);
			} catch (IllegalArgumentException e) {
				MineTinker.getPlugin().getLogger()
						.warning("Illegal material name found when loading Drilling blacklist: " + mat);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUseDrilling(Player player, ItemStack tool, Location loc) {
		if (!player.hasPermission("minetinker.modifiers.drilling.use")) {
			return false;
		}

		if (events.remove(loc, 0)) {
			return false;
		}

		if (toggleable) {
			if (player.isSneaking()) {
				return false;
			}
		}

		return modManager.hasMod(tool, this);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(WorldSaveEvent e) {
		if (Bukkit.getOnlinePlayers().isEmpty()) {
			events.clear();
		}
	}

	/**
	 * The effect when a Block was brocken
	 *
	 * @param event The Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(MTBlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		Block block = event.getBlock();

		if (!canUseDrilling(player, tool, block.getLocation())) {
			return;
		}

		final int level = modManager.getModLevel(tool, this);

		// Check if power save the old correct blockface
		BlockFace face = Power.drillingCommunication.remove(block.getLocation());
		boolean usedPowerCommunication = true;
		if (!(modManager.hasMod(tool, Power.instance()) && face != null)) {
			face = Lists.BLOCKFACE.get(player);
			usedPowerCommunication = false;
		}

		final float hardness = block.getType().getHardness();

		BlockFace finalFace = face.getOppositeFace();
		Bukkit.getScheduler().runTask(MineTinker.getPlugin(), () -> {
			for (int i = 1; i <= level; i++) {
				if (!drillingBlockBreak(block.getRelative(finalFace, i),
						hardness, player, tool)) break;
			}
		});

		ChatWriter.logModifier(player, event, this, tool,
				"Block(" + block.getType() + ")",
				"Blockface(" + face.toString() + (usedPowerCommunication ? "[Power]" : "") + ")");
	}

	private boolean drillingBlockBreak(final Block block, final float centralBlockHardness, final Player player, final ItemStack tool) {
		if (treatAsWhitelist ^ blacklist.contains(block.getType())) {
			return false;
		}

		if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) {
			return false;
		}

		if (block.getType().getHardness() > centralBlockHardness + 2) {
			return false; //So Obsidian can not be mined using Cobblestone and Drilling
		}

		try {
			events.put(block.getLocation(), 0);
			if (modManager.hasMod(tool, Power.instance()))
				Power.events.put(block.getLocation(), 0);
			return DataHandler.playerBreakBlock(player, block, tool);
		} catch (IllegalArgumentException ignored) {
			return false;
		}
	}
}

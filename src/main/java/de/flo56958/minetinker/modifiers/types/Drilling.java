package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Drilling extends Modifier implements Listener {

	public static final ConcurrentHashMap<Location, Integer> events_break = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Location, Integer> events_interact = new ConcurrentHashMap<>();

	private static Drilling instance;
	private HashSet<Material> blacklist = new HashSet<>();
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
		return Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.HOE, ToolType.SHEARS);
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
		config.addDefault("ModifierItemMaterial", Material.HOPPER.name());

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

		final Set<String> blacklistTemp = new HashSet<>();

		for (Material value : Material.values()) {
			if (value.isAir())
				blacklistTemp.add(value.name());
		}
		blacklistTemp.add(Material.BEDROCK.name());
		blacklistTemp.add(Material.WATER.name());
		blacklistTemp.add(Material.BUBBLE_COLUMN.name());
		blacklistTemp.add(Material.LAVA.name());
		blacklistTemp.add(Material.END_PORTAL.name());
		blacklistTemp.add(Material.END_CRYSTAL.name());
		blacklistTemp.add(Material.END_PORTAL_FRAME.name());
		blacklistTemp.add(Material.NETHER_PORTAL.name());

		config.addDefault("Blacklist", new ArrayList<>(blacklistTemp));
		config.addDefault("TreatAsWhitelist", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.toggleable = config.getBoolean("Toggleable", true);
		this.treatAsWhitelist = config.getBoolean("TreatAsWhitelist", false);

		blacklist.clear();

		final List<String> blacklistConfig = config.getStringList("Blacklist");
		blacklist.addAll(blacklistConfig.stream().map(Material::getMaterial).toList());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUseDrilling(Player player, ItemStack tool, Location loc) {
		if (!player.hasPermission(getUsePermission())) return false;
		if (toggleable && player.isSneaking()) return false;

		return modManager.hasMod(tool, this);
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull final WorldSaveEvent event) {
		events_break.clear();
		events_interact.clear();
	}

	// No ignoreCancelled as Building will cancel Drilling
	@EventHandler
	public void effect(@NotNull final MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (events_interact.remove(block.getLocation(), 0)) return; // check first to clear map
		if (block.getType().isAir()) return; // does not work with air
		if (block.isLiquid()) return; // does not work with liquids

		if (!canUseDrilling(player, tool, block.getLocation())) return;

		BlockFace face = event.getEvent().getBlockFace();
		if (event.getEvent().getAction() == Action.RIGHT_CLICK_BLOCK && modManager.hasMod(tool, Building.instance()))
			face = face.getOppositeFace();

		final boolean power = modManager.hasMod(tool, Power.instance());

		final int level = modManager.getModLevel(tool, this);
		final BlockFace finalFace = face.getOppositeFace();
		for (int i = 1; i <= level; i++) {
			final Block b = block.getRelative(finalFace, i);
			if (block.getType().isAir()) break;
			if (block.isLiquid()) break;
			if (events_interact.putIfAbsent(b.getLocation(), 0) != null) break;

			if (power)
				Power.events_interact.putIfAbsent(b.getLocation(), 0);

			Bukkit.getPluginManager().callEvent(
					new PlayerInteractEvent(player, event.getEvent().getAction(), tool, b, event.getEvent().getBlockFace()));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull final MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Block block = event.getBlock();
		if (events_break.remove(block.getLocation(), 0)) return; // check first to clear map
		if (blacklist.contains(block.getType())) return;
		if (block.getType().isAir()) return; // does not work with air
		if (block.isLiquid()) return; // does not work with liquids
		if (!canUseDrilling(player, tool, block.getLocation())) return;

		final int level = modManager.getModLevel(tool, this);

		// Check if power save the old correct blockface
		BlockFace face = Power.drillingCommunication.remove(block.getLocation());
		boolean usedPowerCommunication = true;
		final boolean power = modManager.hasMod(tool, Power.instance());
		if (face == null) {
			face = event.getBlockFace();
			usedPowerCommunication = false;
		}

		final float hardness = block.getType().getHardness();

		final BlockFace finalFace = face.getOppositeFace();
		for (int i = 1; i <= level; i++) {
			if (!drillingBlockBreak(block.getRelative(finalFace, i),
					hardness, player, tool, power)) break;
		}
		ChatWriter.logModifier(player, event, this, tool,
				"Block(" + block.getType() + ")",
				"Blockface(" + face + (usedPowerCommunication ? "[Power]" : "") + ")");
	}

	private boolean drillingBlockBreak(final Block block, final float centralBlockHardness, final Player player, final ItemStack tool, final boolean power) {
		if (block == null) return false;
		if (treatAsWhitelist ^ blacklist.contains(block.getType())) return false;
		if (block.getType().isAir()) return false;
		if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) return false;
		//So Obsidian can not be mined using Cobblestone and Drilling
		if (player.getGameMode() != GameMode.CREATIVE && block.getType().getHardness() > centralBlockHardness + 2) return false;

		try {
			events_break.putIfAbsent(block.getLocation(), 0);
			if (power)
				Power.events_break.putIfAbsent(block.getLocation(), 0);
			return DataHandler.playerBreakBlock(player, block, tool);
		} catch (IllegalArgumentException ignored) {
			return false;
		}
	}
}

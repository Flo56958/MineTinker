package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.PlayerConfigurableModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.PlayerInfo;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationOption;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Power extends PlayerConfigurableModifier implements Listener {

	public static final ConcurrentHashMap<Location, Integer> events_break = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Location, Integer> events_interact = new ConcurrentHashMap<>();

	//Communicates the used BlockFace to the Drilling Modifier
	public static final ConcurrentHashMap<Location, BlockFace> drillingCommunication = new ConcurrentHashMap<>();
	private static Power instance;
	private HashSet<Material> blacklist = new HashSet<>();
	private boolean treatAsWhitelist;
	private boolean toggleable;

	private Power() {
		super(MineTinker.getPlugin());
		customModelData = 10_027;
	}

	public static Power instance() {
		synchronized (Power.class) {
			if (instance == null)
				instance = new Power();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Power";
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
		config.addDefault("MaxLevel", 2); // Algorithm for area of effect (except for level 1): (level * 2) - 1 x
		config.addDefault("SlotCost", 2);
		config.addDefault("ModifierItemMaterial", Material.EMERALD.name());

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

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

		CLAMP_LEVEL = new PlayerConfigurationOption(this, "clamp-to-level", PlayerConfigurationOption.Type.INTEGER,
				LanguageManager.getString("Modifier.Power.PCO_clamp_level"), this.getMaxLvl());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUsePower(final Player player, final ItemStack tool) {
		if (!player.hasPermission(getUsePermission())) return false;
		if (toggleable && player.isSneaking()) return false;

		return modManager.hasMod(tool, this);
	}

	private Set<Block> getPowerBlocks(@NotNull final Player player, @NotNull final ItemStack tool, @NotNull final Block block, @NotNull final BlockFace face, @NotNull final PlayerInfo.Direction direction) {
		final int level = Math.min(modManager.getModLevel(tool, this), PlayerConfigurationManager.getInstance().getInteger(player, CLAMP_LEVEL));

		final HashSet<Block> blocks = new HashSet<>();

		final boolean down_up = face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP);
		final boolean north_south = face.equals(BlockFace.NORTH) || face.equals(BlockFace.SOUTH);
		if (level == 1) {
			Block b1 = null, b2 = null;
			if (PlayerConfigurationManager.getInstance().getBoolean(player, LEVEL_1_VERTICAL)) {
				if (down_up) {
					if (direction == PlayerInfo.Direction.NORTH || direction == PlayerInfo.Direction.SOUTH) {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
					} else if (direction == PlayerInfo.Direction.WEST || direction == PlayerInfo.Direction.EAST) {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
					}
				} else {
					b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
					b2 = block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0));
				}
			} else if (down_up) {
				if (direction == PlayerInfo.Direction.NORTH || direction == PlayerInfo.Direction.SOUTH) {
					b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
					b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
				} else if (direction == PlayerInfo.Direction.WEST || direction == PlayerInfo.Direction.EAST) {
					b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
					b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
				}
			} else if (north_south) {
				b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
				b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
			} else if (face.equals(BlockFace.WEST) || face.equals(BlockFace.EAST)) {
				b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
				b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
			}
			blocks.add(b1);
			blocks.add(b2);
		} else if (level > 1) {
			if (down_up) {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						if (x != 0 || z != 0) {
							Block b = block.getWorld().getBlockAt(block.getLocation().add(x, 0, z));
							blocks.add(b);
						}
					}
				}
			} else if (north_south) {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int y = -(level - 1); y <= (level - 1); y++) {
						if (x != 0 || y != 0) {
							Block b = block.getWorld().getBlockAt(block.getLocation().add(x, y, 0));
							blocks.add(b);
						}
					}
				}
			} else if (face.equals(BlockFace.EAST) || face.equals(BlockFace.WEST)) {
				for (int z = -(level - 1); z <= (level - 1); z++) {
					for (int y = -(level - 1); y <= (level - 1); y++) {
						if (y != 0 || z != 0) {
							Block b = block.getWorld().getBlockAt(block.getLocation().add(0, y, z));
							blocks.add(b);
						}
					}
				}
			}
		}

		return blocks;
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(WorldSaveEvent e) {
		events_break.clear();
		events_interact.clear();
		drillingCommunication.clear();
	}

	/**
	 * The effect when a Block was broken
	 *
	 * @param event The Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull final MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Block block = event.getBlock();

		//Was the block broken by Power or Drilling, blocks unwanted recursion
		if (events_break.remove(block.getLocation(), 0)) return; // check first to clear map
		if (block.getType().isAir()) return; // does not work with air
		if (block.isLiquid()) return; // does not work with liquids

		if (!canUsePower(player, tool)) return;

		final PlayerInfo.Direction direction = PlayerInfo.getFacingDirection(player);
		final BlockFace face = event.getBlockFace();

		final float hardness = block.getType().getHardness();

		final boolean drilling = modManager.hasMod(tool, Drilling.instance());

		for (final Block b : getPowerBlocks(player, tool, block, face, direction)) {
			if (b.getType().isAir()) continue;
			if (b.isLiquid()) continue;

			powerBlockBreak(b, hardness, player, tool, face, drilling);
		}

		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType() + ")");
	}

	// No ignoreCancelled as Building will cancel Power
	@EventHandler(priority = EventPriority.HIGH) // High so it is called after the interactions below
	public void onInteract(@NotNull final MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();

		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (events_interact.remove(block.getLocation(), 0)) return; // check first to clear map
		if (block.getType().isAir()) return; // does not work with air
		if (block.isLiquid()) return; // does not work with liquids

		if (!canUsePower(player, tool)) return;

		final BlockFace face = event.getEvent().getBlockFace();

		// Broadcast InteractEvent to other Blocks
		final Set<Block> blocks = getPowerBlocks(player, tool, block, face, PlayerInfo.getFacingDirection(player));
		blocks.remove(block); // Remove the central block (already handled in the BlockBreakEvent)
		for (final Block b : blocks) {
			if (b.getType().isAir()) continue;
			if (b.isLiquid()) continue;
			if (events_interact.putIfAbsent(b.getLocation(), 0) != null) continue;

			Bukkit.getPluginManager().callEvent(
							new PlayerInteractEvent(player, event.getEvent().getAction(), tool, b, event.getEvent().getBlockFace()));
		}
	}

	/**
	 * Effect for the PlayerInteractEvent for the Hoe
	 */
	@EventHandler(ignoreCancelled = true)
	public void createFarmland(@NotNull final MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		if (!ToolType.HOE.contains(tool.getType())) return;

		final Block block = event.getEvent().getClickedBlock();
		if (!(event.getEvent().getAction() == Action.RIGHT_CLICK_BLOCK && block != null)) return;
		if (!(block.getType() == Material.GRASS_BLOCK
				|| block.getType() == Material.DIRT || block.getType() == Material.DIRT_PATH)) return;

		final Block upperBlock = player.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
		if (!upperBlock.getType().isAir() && !Tag.MAINTAINS_FARMLAND.isTagged(upperBlock.getType()))
			//Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
			return;

		if (!events_interact.containsKey(block.getLocation())) return;
		if (!canUsePower(player, tool)) return;

		// central block exp does not give exp TODO: fix
		modManager.addExp(player, tool,
				MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);

		ChatWriter.logModifier(player, event, this, tool);

		if (!DataHandler.triggerItemDamage(player, tool, 1)) return;

		block.setType(Material.FARMLAND); // Event only does Plugin event (no vanilla conversion to Farmland and Tool-Damage)
	}

	/**
	 * Effect for the PlayerInteractEvent for the Shovel
	 */
	@EventHandler(ignoreCancelled = true)
	public void createGrasspath(@NotNull final MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = player.getInventory().getItemInMainHand();
		if (!ToolType.SHOVEL.contains(tool.getType())) return;

		final Block block = event.getEvent().getClickedBlock();
		if (!(event.getEvent().getAction() == Action.RIGHT_CLICK_BLOCK && block != null)) return;
		if (!(block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT)) return;

		final Block upperBlock = player.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
		if (!upperBlock.getType().isAir())
			//Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
			return;

		if (!events_interact.containsKey(block.getLocation())) return;
		if (!canUsePower(player, tool)) return;

		// central block exp does not give exp TODO: fix
		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);

		ChatWriter.logModifier(player, event, this, tool);

		if (!DataHandler.triggerItemDamage(player, tool, 1)) return;

		block.setType(Material.DIRT_PATH); // Event only does Plugin event (no vanilla conversion to Pathway and Tool-Damage)
	}

	@EventHandler(ignoreCancelled = true)
	public void createStrippedLogs(@NotNull final MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = player.getInventory().getItemInMainHand();
		if (!ToolType.AXE.contains(tool.getType())) return;

		final Block block = event.getEvent().getClickedBlock();
		if (!(event.getEvent().getAction() == Action.RIGHT_CLICK_BLOCK && block != null
				&& (Tag.LOGS.isTagged(block.getType()) && !block.getType().name().contains("STRIPPED_"))))
			return;

		if (events_interact.containsKey(block.getLocation()) && canUsePower(player, tool)) {
			ChatWriter.logModifier(player, event, this, tool);

			final Material log = Material.getMaterial("STRIPPED_" + block.getType().name());
			if (log == null) return;

			if (!DataHandler.triggerItemDamage(player, tool, 1)) return;

			block.setType(log); // Event only does Plugin event (no vanilla conversion to stripped and Tool-Damage)
		}

		// Also handles central block exp (if Power is disabled this won't be called TODO: fix)
		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);
	}

	private void powerBlockBreak(@Nullable final Block block, final float centralBlockHardness, final Player player, final ItemStack tool, final BlockFace face, final boolean drilling) {
		if (block == null) return;

		if (treatAsWhitelist ^ blacklist.contains(block.getType())) return;
		if (block.getDrops(tool).isEmpty()) return;
		if (player.getGameMode() != GameMode.CREATIVE && block.getType().getHardness() > centralBlockHardness + 2) // + 2 so you can mine ore as well
			return; //So Obsidian can not be mined using Cobblestone and Power
		if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() != ageable.getMaximumAge()) return;
		if (!(block.getBlockData() instanceof Ageable) && ToolType.HOE.contains(tool.getType())) return;

		// Save BlockFace so Drilling can use the 'old' information
		if (drilling) drillingCommunication.put(block.getLocation(), face);

		if (events_break.putIfAbsent(block.getLocation(), 0) != null) return;

		try {
			DataHandler.playerBreakBlock(player, block, tool);
		} catch (IllegalArgumentException ignored) {}
	}

	private PlayerConfigurationOption LEVEL_1_VERTICAL =
			new PlayerConfigurationOption(this, "level-1-vertical", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Power.PCO_level_1_vertical"), false);

	private PlayerConfigurationOption CLAMP_LEVEL;

	@Override
	public List<PlayerConfigurationOption> getPCIOptions() {
		final ArrayList<PlayerConfigurationOption> playerConfigurationOptions = new ArrayList<>(List.of(LEVEL_1_VERTICAL, CLAMP_LEVEL));
		playerConfigurationOptions.sort(Comparator.comparing(PlayerConfigurationOption::displayName));
		return playerConfigurationOptions;
	}
}

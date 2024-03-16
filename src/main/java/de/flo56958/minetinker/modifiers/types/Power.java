package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.PlayerInfo;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Power extends Modifier implements Listener {

	public static final ConcurrentHashMap<Location, Integer> events = new ConcurrentHashMap<>();

	//Communicates the used BlockFace to the Drilling Modifier
	public static final ConcurrentHashMap<Location, BlockFace> drillingCommunication = new ConcurrentHashMap<>();
	private static Power instance;
	private HashSet<Material> blacklist = new HashSet<>();
	private boolean treatAsWhitelist;
	private boolean lv1_vertical;
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
		return Arrays.asList(ToolType.AXE, ToolType.HOE, ToolType.PICKAXE, ToolType.SHOVEL);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("Lv1Vertical", false); // Should the 3x1 at level 1 be horizontal (false) or vertical (true)
		config.addDefault("Toggleable", true);
		config.addDefault("MaxLevel", 2); // Algorithm for area of effect (except for level 1): (level * 2) - 1 x
		config.addDefault("SlotCost", 2);
		config.addDefault("ModifierItemMaterial", Material.EMERALD.name());

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		final List<String> blacklistTemp = new ArrayList<>();

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

		init();

		this.lv1_vertical = config.getBoolean("Lv1Vertical", false);
		this.toggleable = config.getBoolean("Toggleable", true);
		this.treatAsWhitelist = config.getBoolean("TreatAsWhitelist", false);

		blacklist.clear();

		final List<String> blacklistConfig = config.getStringList("Blacklist");
		blacklist.addAll(blacklistConfig.stream().map(Material::getMaterial).toList());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUsePower(final Player player, final ItemStack tool) {
		if (!player.hasPermission(getUsePermission())) return false;
		if (toggleable && player.isSneaking()) return false;

		return modManager.hasMod(tool, this);
	}

	private HashSet<Block> getPowerBlocks(@NotNull ItemStack tool, @NotNull Block block, @NotNull BlockFace face, @NotNull PlayerInfo.Direction direction) {
		final int level = modManager.getModLevel(tool, this);

		final HashSet<Block> blocks = new HashSet<>();

		final boolean down_up = face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP);
		final boolean north_south = face.equals(BlockFace.NORTH) || face.equals(BlockFace.SOUTH);
		if (level == 1) {
			Block b1 = null, b2 = null;
			if (lv1_vertical) {
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
		} else {
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
		if (!Bukkit.getOnlinePlayers().isEmpty()) return;

		events.clear();
		drillingCommunication.clear();
	}

	/**
	 * The effect when a Block was broken
	 *
	 * @param event The Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Block block = event.getBlock();

		if (!canUsePower(player, tool)) return;

		//Was the block broken by Power or Drilling, blocks unwanted recursion
		if (events.remove(block.getLocation(), 0)) return;

		final PlayerInfo.Direction direction = PlayerInfo.getFacingDirection(player);
		final BlockFace face = Lists.BLOCKFACE.get(player);

		final float hardness = block.getType().getHardness();

		for (final Block b : getPowerBlocks(tool, block, face, direction)) {
			powerBlockBreak(b, hardness, player, tool, face);
		}

		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType() + ")");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInteract(@NotNull MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		if (!canUsePower(player, tool)) return;

		// Broadcast InteractEvent to other Blocks
		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (events.remove(block.getLocation(), 0)) return;

		for (final Block b : getPowerBlocks(tool, block, Lists.BLOCKFACE.getOrDefault(player, event.getEvent().getBlockFace()), PlayerInfo.getFacingDirection(player))) {
			if (b.getType().isAir()) continue;
			if (events.putIfAbsent(b.getLocation(), 0) != null) continue;

			Bukkit.getScheduler().runTaskLater(this.getSource(),
					() -> Bukkit.getPluginManager().callEvent(
							new PlayerInteractEvent(player, event.getEvent().getAction(), tool, b, event.getEvent().getBlockFace())),
					1);
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
		if (!upperBlock.getType().isAir())
			//Case Block is on top of clicked Block -> No Soil Tilt -> no Exp
			return;

		// Also handles central block exp (if Power is disabled this won't be called TODO: fix)
		modManager.addExp(player, tool,
				MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);

		if (!canUsePower(player, tool)) return;
		if (!events.containsKey(block.getLocation())) return;

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

		// Also handles central block exp (if Power is disabled this won't be called TODO: fix)
		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);

		if (!canUsePower(player, tool)) return;
		if (!events.containsKey(block.getLocation())) return;

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
				&& (Lists.getWoodLogs().contains(block.getType()) || Lists.getWoodWood().contains(block.getType()))))
			return;

		if (canUsePower(player, tool) && events.containsKey(block.getLocation())) {
			ChatWriter.logModifier(player, event, this, tool);

			final Material log = Material.getMaterial("STRIPPED_" + block.getType().name());
			if (log == null) return;

			if (!DataHandler.triggerItemDamage(player, tool, 1)) return;

			block.setType(log); // Event only does Plugin event (no vanilla conversion to stripped and Tool-Damage)
		}

		// Also handles central block exp (if Power is disabled this won't be called TODO: fix)
		modManager.addExp(player, tool, MineTinker.getPlugin().getConfig().getInt("ExpPerBlockBreak"), true);
	}

	private void powerBlockBreak(@Nullable final Block block, final float centralBlockHardness, final Player player, final ItemStack tool, final BlockFace face) {
		if (block == null) return;

		Bukkit.getScheduler().runTask(this.getSource(), () -> {
			if (treatAsWhitelist ^ blacklist.contains(block.getType())) return;
			if (block.getDrops(tool).isEmpty()) return;
			if (block.getType().getHardness() > centralBlockHardness + 2) // + 2 so you can mine ore as well
				return; //So Obsidian can not be mined using Cobblestone and Power
			if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() != ageable.getMaximumAge()) return;
			if (!(block.getBlockData() instanceof Ageable) && ToolType.HOE.contains(tool.getType())) return;

			events.put(block.getLocation(), 0);

			// Save BlockFace so Drilling can use the 'old' information
			if (modManager.hasMod(tool, Drilling.instance())) drillingCommunication.put(block.getLocation(), face);

			try {
				DataHandler.playerBreakBlock(player, block, tool);
			} catch (IllegalArgumentException ignored) {}
		});
	}
}

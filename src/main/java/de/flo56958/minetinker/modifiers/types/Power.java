package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.api.serverhandler.ServerHandler;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Power extends Modifier implements Listener {

	public static final ConcurrentHashMap<Player, AtomicBoolean> HAS_POWER = new ConcurrentHashMap<>();
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

	private void powerCreateFarmland(Player player, ItemStack tool, Block block) {
		if (block.getType().equals(Material.GRASS_BLOCK) || block.getType().equals(Material.DIRT)) {
			if (block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).getType().equals(Material.AIR)) {
				if (tool.getItemMeta() instanceof Damageable damageable) {
					damageable.setDamage(damageable.getDamage() + 1);
					tool.setItemMeta(damageable);
				}

				events.put(block.getLocation(), 0);

				PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK,
						tool, block, BlockFace.UP);
				Bukkit.getPluginManager().callEvent(event);

				block.setType(Material.FARMLAND); // Event only does Plugin event (no vanilla conversion to Farmland and
				// Tool-Damage)
			}
		}
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

		init(Material.EMERALD);

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
		if (ToolType.HOE.contains(tool.getType())) return;

		final PlayerInfo.Direction direction = PlayerInfo.getFacingDirection(player);
		final BlockFace face = Lists.BLOCKFACE.get(player);

		final float hardness = block.getType().getHardness();

		ServerHandler.getServerHandler().runTaskAsynchronously(() -> {
			int level = modManager.getModLevel(tool, this);

			boolean down_up = face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP);
			boolean north_south = face.equals(BlockFace.NORTH) || face.equals(BlockFace.SOUTH);
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
				powerBlockBreak(b1, hardness, player, tool, face);
				powerBlockBreak(b2, hardness, player, tool, face);
			} else {
				if (down_up) {
					for (int x = -(level - 1); x <= (level - 1); x++) {
						for (int z = -(level - 1); z <= (level - 1); z++) {
							if (x != 0 || z != 0) {
								Block b = block.getWorld().getBlockAt(block.getLocation().add(x, 0, z));
								powerBlockBreak(b, hardness, player, tool, face);
							}
						}
					}
				} else if (north_south) {
					for (int x = -(level - 1); x <= (level - 1); x++) {
						for (int y = -(level - 1); y <= (level - 1); y++) {
							if (x != 0 || y != 0) {
								Block b = block.getWorld().getBlockAt(block.getLocation().add(x, y, 0));
								powerBlockBreak(b, hardness, player, tool, face);
							}
						}
					}
				} else if (face.equals(BlockFace.EAST) || face.equals(BlockFace.WEST)) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						for (int y = -(level - 1); y <= (level - 1); y++) {
							if (y != 0 || z != 0) {
								Block b = block.getWorld().getBlockAt(block.getLocation().add(0, y, z));
								powerBlockBreak(b, hardness, player, tool, face);
							}
						}
					}
				}
			}
		});

		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType() + ")");
	}

	/**
	 * Effect for the PlayerInteractEvent for the Hoe
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull MTPlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();

		if (!ToolType.HOE.contains(tool.getType())) return;
		if (!canUsePower(player, tool)) return;
		ChatWriter.logModifier(player, event, this, tool);

		final int level = modManager.getModLevel(tool, this);
		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (events.remove(block.getLocation(), 0)) return;

		if (level == 1) {
			if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
				Block b1;
				Block b2;
				final PlayerInfo.Direction direction = PlayerInfo.getFacingDirection(player);
				if (direction == PlayerInfo.Direction.NORTH || direction == PlayerInfo.Direction.SOUTH) {
					if (this.lv1_vertical) {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
					} else {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
					}
				} else if (direction == PlayerInfo.Direction.WEST || direction == PlayerInfo.Direction.EAST) {
					if (this.lv1_vertical) {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
					} else {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
					}
				} else {
					b1 = block;
					b2 = block;
				}

				powerCreateFarmland(player, tool, b1);
				powerCreateFarmland(player, tool, b2);
			}
		} else {
			if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						if (!(x == 0 && z == 0)) {
							final Block b_ = player.getWorld().getBlockAt(block.getLocation().add(x, 0, z));
							powerCreateFarmland(player, tool, b_);
						}
					}
				}
			}
		}
	}

	private void powerBlockBreak(@Nullable final Block block, final float centralBlockHardness, final Player player, final ItemStack tool, final BlockFace face) {
		if (block == null) return;

		ServerHandler.getServerHandler().runTask(() -> {
			if (treatAsWhitelist ^ blacklist.contains(block.getType())) return;
			if (block.getDrops(tool).isEmpty()) return;
			if (block.getType().getHardness() > centralBlockHardness + 2) // + 2 so you can mine ore as well
				return; //So Obsidian can not be mined using Cobblestone and Power

			events.put(block.getLocation(), 0);

			// Save BlockFace so Drilling can use the 'old' information
			if (modManager.hasMod(tool, Drilling.instance())) drillingCommunication.put(block.getLocation(), face);

			try {
				DataHandler.playerBreakBlock(player, block, tool);
			} catch (IllegalArgumentException ignored) {}
		});
	}
}

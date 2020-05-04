package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Events.MTPlayerInteractEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.PlayerInfo;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Power extends Modifier implements Listener {

	public static final ConcurrentHashMap<Player, AtomicBoolean> HASPOWER = new ConcurrentHashMap<>();
	private static Power instance;
	private ArrayList<Material> blacklist;
	private boolean lv1_vertical;
	private boolean toggleable;

	private Power() {
		super(Main.getPlugin());
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
				if (tool.getItemMeta() instanceof Damageable) {
					Damageable damageable = (Damageable) tool.getItemMeta();
					damageable.setDamage(damageable.getDamage() + 1);
					tool.setItemMeta((ItemMeta) damageable);
				}

				PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, tool, block, BlockFace.UP);
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
		config.addDefault("Name", "Power");
		config.addDefault("ModifierItemName", "Enchanted Emerald");
		config.addDefault("Description", "Tool can destroy more blocks per swing!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Power-Modifier");
		config.addDefault("Color", "%GREEN%");
		config.addDefault("Lv1Vertical", false); // Should the 3x1 at level 1 be horizontal (false) or vertical
		// (true)
		config.addDefault("Toggleable", true);
		config.addDefault("OverrideLanguagesystem", false);
		config.addDefault("MaxLevel", 2); // Algorithm for area of effect (except for level 1): (level * 2) - 1 x
		config.addDefault("SlotCost", 2);

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);

		config.addDefault("Recipe.Enabled", false);

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

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.EMERALD, true);

		this.lv1_vertical = config.getBoolean("Lv1Vertical", false);
		this.toggleable = config.getBoolean("Toggleable", true);

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
				Main.getPlugin().getLogger()
						.warning("Illegal material name found when loading Power blacklist: " + mat);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUsePower(Player player, ItemStack tool) {
		if (!player.hasPermission("minetinker.modifiers.power.use")) {
			return false;
		}

		if (HASPOWER.get(player).get()) {
			return false;
		}

		if (toggleable) {
			if (player.isSneaking()) {
				return false;
			}
		}

		return modManager.hasMod(tool, this);
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

		if (!canUsePower(player, tool)) {
			return;
		}

		if (ToolType.HOE.contains(tool.getType())) {
			return;
		}

		ChatWriter.log(false, player.getDisplayName() + " triggered Power on " + ChatWriter.getDisplayName(tool)
				+ ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

		HASPOWER.get(player).set(true); // for the power-triggered BlockBreakEvents (prevents endless "recursion")

		int level = modManager.getModLevel(tool, this);

		if (level == 1) {
			if (lv1_vertical) {
				if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
					if (PlayerInfo.getFacingDirection(player).equals("N") || PlayerInfo.getFacingDirection(player).equals("S")) {
						Block b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
						Block b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
						powerBlockBreak(b1, block, player);
						powerBlockBreak(b2, block, player);
					} else if (PlayerInfo.getFacingDirection(player).equals("W")
							|| PlayerInfo.getFacingDirection(player).equals("E")) {
						Block b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
						Block b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
						powerBlockBreak(b1, block, player);
						powerBlockBreak(b2, block, player);
					}
				} else {
					Block b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
					Block b2 = block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0));
					powerBlockBreak(b1, block, player);
					powerBlockBreak(b2, block, player);
				}
			} else if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
				if (PlayerInfo.getFacingDirection(player).equals("N") || PlayerInfo.getFacingDirection(player).equals("S")) {
					Block b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
					Block b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
					powerBlockBreak(b1, block, player);
					powerBlockBreak(b2, block, player);
				} else if (PlayerInfo.getFacingDirection(player).equals("W")
						|| PlayerInfo.getFacingDirection(player).equals("E")) {
					Block b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
					Block b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
					powerBlockBreak(b1, block, player);
					powerBlockBreak(b2, block, player);
				}
			} else if (Lists.BLOCKFACE.get(player).equals(BlockFace.NORTH)
					|| Lists.BLOCKFACE.get(player).equals(BlockFace.SOUTH)) {
				Block b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
				Block b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
				powerBlockBreak(b1, block, player);
				powerBlockBreak(b2, block, player);
			} else if (Lists.BLOCKFACE.get(player).equals(BlockFace.WEST) || Lists.BLOCKFACE.get(player).equals(BlockFace.EAST)) {
				Block b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
				Block b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
				powerBlockBreak(b1, block, player);
				powerBlockBreak(b2, block, player);
			}
		} else {
			HASPOWER.get(player).set(true);

			if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int z = -(level - 1); z <= (level - 1); z++) {
						if (!(x == 0 && z == 0)) {
							Block b1 = block.getWorld().getBlockAt(block.getLocation().add(x, 0, z));
							powerBlockBreak(b1, block, player);
						}
					}
				}
			} else if (Lists.BLOCKFACE.get(player).equals(BlockFace.NORTH)
					|| Lists.BLOCKFACE.get(player).equals(BlockFace.SOUTH)) {
				for (int x = -(level - 1); x <= (level - 1); x++) {
					for (int y = -(level - 1); y <= (level - 1); y++) {
						if (!(x == 0 && y == 0)) {
							Block b1 = block.getWorld().getBlockAt(block.getLocation().add(x, y, 0));
							powerBlockBreak(b1, block, player);
						}
					}
				}
			} else if (Lists.BLOCKFACE.get(player).equals(BlockFace.EAST) || Lists.BLOCKFACE.get(player).equals(BlockFace.WEST)) {
				for (int z = -(level - 1); z <= (level - 1); z++) {
					for (int y = -(level - 1); y <= (level - 1); y++) {
						if (!(z == 0 && y == 0)) {
							Block b1 = block.getWorld().getBlockAt(block.getLocation().add(0, y, z));
							powerBlockBreak(b1, block, player);
						}
					}
				}
			}
		}

		HASPOWER.get(player).set(false); // so the effect of power is not disabled for the Player
	}

	/**
	 * Effect for the PlayerInteractEvent for the Hoe
	 */
	@EventHandler (ignoreCancelled = true)
	public void effect(MTPlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!ToolType.HOE.contains(tool.getType())) {
			return;
		}

		PlayerInteractEvent interactEvent = event.getEvent();

		if (!canUsePower(player, tool)) {
			return;
		}

		ChatWriter.log(false, player.getDisplayName() + " triggered Power on " + ChatWriter.getDisplayName(tool)
				+ ChatColor.GRAY + " (" + tool.getType().toString() + ")!");

		HASPOWER.get(player).set(true);

		int level = modManager.getModLevel(tool, this);
		Block block = interactEvent.getClickedBlock();

		if (block == null) {
			return;
		}

		if (level == 1) {
			if (Lists.BLOCKFACE.get(player).equals(BlockFace.DOWN) || Lists.BLOCKFACE.get(player).equals(BlockFace.UP)) {
				Block b1;
				Block b2;

				if ((PlayerInfo.getFacingDirection(player).equals("N") || PlayerInfo.getFacingDirection(player).equals("S"))) {
					if (this.lv1_vertical) {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
					} else {
						b1 = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
						b2 = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
					}
				} else if (PlayerInfo.getFacingDirection(player).equals("W")
						|| PlayerInfo.getFacingDirection(player).equals("E")) {
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
							Block b_ = player.getWorld().getBlockAt(block.getLocation().add(x, 0, z));
							powerCreateFarmland(player, tool, b_);
						}
					}
				}
			}
		}

		HASPOWER.get(player).set(false);
	}

	private void powerBlockBreak(Block block, Block centralBlock, Player player) {
		if (blacklist.contains(block.getType())) {
			return;
		}

		if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) {
			return;
		}

		if (block.getType().getHardness() > centralBlock.getType().getHardness() + 2) {
			return; //So Obsidian can not be mined using Cobblestone and Power
		}

		NBTUtils.getHandler().playerBreakBlock(player, block);
	}
}

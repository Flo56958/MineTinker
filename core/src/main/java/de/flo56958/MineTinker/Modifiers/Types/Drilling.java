package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.Lists;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTBlockBreakEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import de.flo56958.MineTinker.Utilities.nms.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Drilling extends Modifier implements Listener {

	public static final ConcurrentHashMap<Player, AtomicBoolean> HASDRILLING = new ConcurrentHashMap<>();
	private static Drilling instance;
	private ArrayList<Material> blacklist;
	private boolean treatAsWhitelist;
	private boolean toggleable;

	private Drilling() {
		super(Main.getPlugin());
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
		config.addDefault("Name", "Drilling");
		config.addDefault("ModifierItemName", "Reinforced Drill");
		config.addDefault("Description", "Drill one block deeper into the earth per level!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Drilling-Modifier");
		config.addDefault("Color", "%GREEN%");
		config.addDefault("Toggleable", true);
		config.addDefault("OverrideLanguagesystem", false);
		config.addDefault("MaxLevel", 4);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", false);

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

		init(Material.HOPPER, true);

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
				Main.getPlugin().getLogger()
						.warning("Illegal material name found when loading Power blacklist: " + mat);
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canUseDrilling(Player player, ItemStack tool) {
		if (!player.hasPermission("minetinker.modifiers.drilling.use")) {
			return false;
		}

		if (HASDRILLING.get(player).get()) {
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

		if (!canUseDrilling(player, tool)) {
			return;
		}

		ChatWriter.log(false, player.getDisplayName() + " triggered Drilling on " + ChatWriter.getDisplayName(tool)
				+ ChatColor.GRAY + " (" + tool.getType().toString() + ")!");


		int level = modManager.getModLevel(tool, this);

		HASDRILLING.get(player).set(true); // for the drilling-triggered BlockBreakEvents (prevents endless "recursion")

		BlockFace face = Lists.BLOCKFACE.get(player).getOppositeFace();
		for (int i = 1; i <= level; i++) {
			if (!drillingBlockBreak(block.getRelative(face, i), block, player)) break;
		}

		HASDRILLING.get(player).set(false); // so the effect of drilling is not disabled for the Player
	}

	private boolean drillingBlockBreak(Block block, Block centralBlock, Player player) {
		if (treatAsWhitelist ^ blacklist.contains(block.getType())) {
			return false;
		}

		if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) {
			return false;
		}

		if (block.getType().getHardness() > centralBlock.getType().getHardness() + 2) {
			return false; //So Obsidian can not be mined using Cobblestone and Power
		}

		NBTUtils.getHandler().playerBreakBlock(player, block);
		return true;
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Timber extends Modifier implements Listener {

	private static Timber instance;
	private int maxBlocks;

	private Timber() {
		super(MineTinker.getPlugin());
		customModelData = 10_042;
	}

	public static Timber instance() {
		synchronized (Timber.class) {
			if (instance == null) {
				instance = new Timber();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Timber";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.SHEARS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 2);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaximumBlocksPerSwing", 2000); //-1 to disable it

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "LLL");
		config.addDefault("Recipe.Middle", "LEL");
		config.addDefault("Recipe.Bottom", "LLL");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("L", Material.OAK_WOOD.name());
		recipeMaterials.put("E", Material.EMERALD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.EMERALD);
		this.maxBlocks = config.getInt("MaximumBlocksPerSwing", 2000);
		this.maxBlocks = (this.maxBlocks == -1) ? Integer.MAX_VALUE : this.maxBlocks;
	}

	@Override
	public boolean applyMod(Player player, ItemStack tool, boolean isCommand) {
		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTBlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		Block block = event.getBlock();

		if (Power.HAS_POWER.get(player).get() || player.isSneaking()) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (!player.hasPermission("minetinker.modifiers.timber.use")) {
			return;
		}

		if (ToolType.SHEARS.contains(tool.getType())) {
			if (Lists.getWoodLeaves().contains(block.getType())) {
				Power.HAS_POWER.get(player).set(true);
				ArrayList<Location> locs = new ArrayList<>();
				locs.add(block.getLocation());
				breakTree(player, tool, block, Collections.singletonList(block.getType()), locs);
			}
		} else {
			ArrayList<Material> allowed = new ArrayList<>();
			allowed.addAll(Lists.getWoodLogs());
			allowed.addAll(Lists.getWoodWood());

			if (!allowed.contains(block.getType())) {
				return;
			}

			boolean isTreeBottom = false; //checks for Grass or Dirt under Log
			boolean isTreeTop = false; //checks for Leaves above Log


			for (int y = block.getY() - 1; y > 0; y--) {
				Material blockType = player.getWorld().getBlockAt(block.getX(), y, block.getZ()).getType();

				if (blockType == Material.GRASS_BLOCK || blockType == Material.DIRT
						|| blockType == Material.PODZOL || blockType == Material.COARSE_DIRT ||
						(MineTinker.is16compatible &&
								(blockType == Material.NETHERRACK
										|| blockType == Material.CRIMSON_NYLIUM
										|| blockType == Material.WARPED_NYLIUM))) {

					isTreeBottom = true;
				}

				if (!player.getWorld().getBlockAt(block.getX(), y, block.getZ()).getType().equals(block.getType())) {
					break;
				}
			}

			for (int dy = block.getY() + 1, airgap = 0; dy < 256 && airgap < 6; dy++) {
				if (!allowed.contains(player.getWorld().getBlockAt(block.getX(), dy, block.getZ()).getType())) {
					Location loc = block.getLocation().clone();
					loc.setY(dy);

					Material mat = player.getWorld().getBlockAt(loc).getType();

					if (Lists.getWoodLeaves().contains(mat)) {
						isTreeTop = true;
					} else if (mat == Material.AIR || mat == Material.CAVE_AIR) {
						airgap++;
						continue;
					}
					break;
				}
			}

			if (!isTreeBottom || !isTreeTop) {
				return; //TODO: Improve tree check
			}

			Power.HAS_POWER.get(player).set(true);
			ArrayList<Location> locs = new ArrayList<>();
			locs.add(block.getLocation());
			breakTree(player, tool, block, allowed, locs);
		}
		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType().toString() + ")");

		Power.HAS_POWER.get(player).set(false);
	}

	private void breakTree(Player player, ItemStack tool, Block block, List<Material> allowed, List<Location> locs) { //TODO: Improve algorithm and performance -> async?
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dy == 0 && dz == 0) {
						continue;
					}

					Location loc = block.getLocation().clone();
					loc.add(dx, dy, dz);

					if (locs.contains(loc)) {
						continue;
					}

					if (locs.size() >= maxBlocks) {
						return;
					}

					locs.add(loc);

					Block toBreak = player.getWorld().getBlockAt(loc);
					if (allowed.contains(toBreak.getType())) {
						breakTree(player, tool, toBreak, allowed, locs);
						try {
							DataHandler.playerBreakBlock(player, block, tool);
						} catch (IllegalArgumentException ignored) {}
					}
				}
			}
		}
	}
}

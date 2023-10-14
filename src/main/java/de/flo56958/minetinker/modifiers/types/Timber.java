package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Timber extends Modifier implements Listener {

	private static Timber instance;
	private int maxBlocks;

	private static final ConcurrentHashMap<Location, Integer> events = new ConcurrentHashMap<>();
	private HashSet<Material> grasses = new HashSet<>();

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
		config.addDefault("MaxLevel", 2);
		config.addDefault("SlotCost", 2);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaximumBlocksPerSwing", 2000); //-1 to disable it

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "LLL");
		config.addDefault("Recipe.Middle", "LEL");
		config.addDefault("Recipe.Bottom", "LLL");

		Map<String, String> recipeMaterials = new HashMap<>();
		StringBuilder mats = new StringBuilder();
		for (final Material mat : Lists.getWoodWood()) {
			mats.append(mat.name()).append(",");
		}
		recipeMaterials.put("L", mats.substring(0, mats.length() - 1));
		recipeMaterials.put("E", Material.EMERALD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.EMERALD);
		this.maxBlocks = config.getInt("MaximumBlocksPerSwing", 2000);
		this.maxBlocks = (this.maxBlocks == -1) ? Integer.MAX_VALUE : this.maxBlocks;

		this.grasses.clear();
		this.grasses.addAll(Arrays.asList(Material.GRASS_BLOCK, Material.DIRT, Material.PODZOL, Material.COARSE_DIRT,
				Material.NETHERRACK, Material.CRIMSON_NYLIUM, Material.WARPED_NYLIUM));
		if (MineTinker.is19compatible) {
			this.grasses.add(Material.MUD);
		}
	}

    @EventHandler(ignoreCancelled = true)
	public void effect(WorldSaveEvent e) {
		if (Bukkit.getOnlinePlayers().isEmpty()) {
			events.clear();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull MTBlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		Block block = event.getBlock();

		if (events.remove(event.getBlock().getLocation(), 0) || player.isSneaking()) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (!player.hasPermission(getUsePermission())) {
			return;
		}

		if (ToolType.SHEARS.contains(tool.getType())) {
			if (Lists.getWoodLeaves().contains(block.getType())) {
				HashSet<Location> locs = new HashSet<>();
				locs.add(block.getLocation());
				Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(),
						() -> breakTree(player, tool, block, new HashSet<>(Collections.singletonList(block.getType())), locs, null));
			}
		} else {
			HashSet<Material> allowed = new HashSet<>();
			allowed.addAll(Lists.getWoodLogs());
			allowed.addAll(Lists.getWoodWood());

			if (!allowed.contains(block.getType())) {
				return;
			}

			boolean isTreeBottom = false; //checks for Grass or Dirt under Log
			boolean isTreeTop = false; //checks for Leaves above Log

			for (int y = block.getY() - 1, airgap = 0; y >= block.getWorld().getMinHeight() && airgap < 10; y--) {
				final Material blockType = player.getWorld().getBlockAt(block.getX(), y, block.getZ()).getType();

				if (this.grasses.contains(blockType)) {
					isTreeBottom = true;
					break;
				}

				if (!allowed.contains(blockType)) {
					// If it is a mangrove tree there can be air below the
					if (MineTinker.is19compatible
							&& (block.getType().equals(Material.MANGROVE_LOG)
							|| block.getType().equals(Material.MANGROVE_ROOTS)
							|| block.getType().equals(Material.MUDDY_MANGROVE_ROOTS))) {
						if (blockType.isAir()
								|| !blockType.isSolid()
								|| blockType.equals(Material.WATER)) {
							airgap++;
							continue;
						}
					}
					break;
				}
			}

			Material saplingType = null;

			//airgap is for trees like acacia
			for (int dy = block.getY() + 1, airgap = 0; dy < block.getWorld().getMaxHeight() && airgap < 10; dy++) {
				if (!allowed.contains(player.getWorld().getBlockAt(block.getX(), dy, block.getZ()).getType())) {
					final Location loc = block.getLocation().clone();
					loc.setY(dy);

					final Block leaves = player.getWorld().getBlockAt(loc);
					final Material mat = leaves.getType();

					if (Lists.getWoodLeaves().contains(mat)) {
						isTreeTop = true;
						saplingType = Material.getMaterial(mat.toString().split("_")[0] + "_SAPLING");
					} else if (mat.isAir() || !mat.isSolid()
							// For mangrove trees
							|| (MineTinker.is19compatible && (mat.equals(Material.MOSS_CARPET) || mat.equals(Material.MANGROVE_PROPAGULE)))) {
						airgap++;
						continue;
					}
					break;
				}
			}

			if (!isTreeBottom || !isTreeTop) {
				return; //TODO: Improve tree check
			}

			final HashSet<Location> locs = new HashSet<>();
			locs.add(block.getLocation());
			final Material finalSaplingType = saplingType;
			Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(), () -> breakTree(player, tool, block, allowed, locs, finalSaplingType));
		}
		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType() + ")");

	}

	private void breakTree(@NotNull Player player, @NotNull ItemStack tool, Block block, HashSet<Material> allowed,
						   @NotNull HashSet<Location> locs, @Nullable Material sapling) {
		//TODO: Improve algorithm and performance
		if (locs.size() >= maxBlocks) {
			return;
		}
		final int level = modManager.getModLevel(tool, this);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dy == 0 && dz == 0) {
						if (level >= 2 && sapling != null) {
							final Block grassBlock = block.getRelative(BlockFace.DOWN);
							if (this.grasses.contains(grassBlock.getType())) {
								for (final ItemStack stack : player.getInventory().getContents()) {
									if (stack == null) continue;
									if (stack.getType() != sapling) continue;

									if (stack.getAmount() >= 1) {
										final Material finalSapling = sapling;
										Runnable runnable = new Runnable() {
											private int counter = 0;
											@Override
											public void run() {
												if (block.getType() != Material.AIR) {
													// Block isn't broken yet
													counter++;
													if (counter <= 10) {
														Bukkit.getScheduler().runTaskLater(MineTinker.getPlugin(), this, 10L);
													}
													return;
												}
												final BlockPlaceEvent placeEvent =
														new BlockPlaceEvent(block, block.getState(), grassBlock, stack,
																player, true, EquipmentSlot.HAND);
												Bukkit.getPluginManager().callEvent(placeEvent);

												//check the pseudoevent
												if (placeEvent.canBuild() && !placeEvent.isCancelled()) {
													stack.setAmount(stack.getAmount() - 1);
													block.setType(finalSapling);
												}
											}
										};
										Bukkit.getScheduler().runTask(MineTinker.getPlugin(), runnable);
										break;
									}
								}
								sapling = null;
							}
						}
						continue;
					}

					final Location loc = block.getLocation();
					loc.add(dx, dy, dz);

					if (locs.contains(loc)) {
						continue;
					}

					locs.add(loc);

					final Block toBreak = player.getWorld().getBlockAt(loc);
					if (allowed.contains(toBreak.getType())) {
						breakTree(player, tool, toBreak, allowed, locs, sapling);
						events.put(toBreak.getLocation(), 0);
						Bukkit.getScheduler().runTask(MineTinker.getPlugin(), () -> {
							try {
								DataHandler.playerBreakBlock(player, toBreak, tool);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						});
					}
				}
			}
		}
	}
}
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

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Timber extends Modifier implements Listener {

	private static Timber instance;
	private int maxBlocks;

	private static final ConcurrentHashMap<Location, Integer> events = new ConcurrentHashMap<>();
	private final HashSet<Material> grasses = new HashSet<>();

	private Timber() {
		super(MineTinker.getPlugin());
		customModelData = 10_042;
	}

	public static Timber instance() {
		synchronized (Timber.class) {
			if (instance == null)
				instance = new Timber();
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
		Lists.getWoodWood().stream().map(Material::name).forEach(mat -> mats.append(mat).append(","));
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
		if (MineTinker.is19compatible)
			this.grasses.add(Material.MUD);
	}

    @EventHandler(ignoreCancelled = true)
	public void effect(WorldSaveEvent e) {
		if (Bukkit.getOnlinePlayers().isEmpty())
			events.clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Block block = event.getBlock();
		if (events.remove(event.getBlock().getLocation(), 0) || player.isSneaking()) return;
		if (!modManager.hasMod(tool, this)) return;
		if (!player.hasPermission(getUsePermission())) return;

		final HashSet<Material> allowed = new HashSet<>();
		allowed.addAll(Lists.getWoodLogs());
		allowed.addAll(Lists.getWoodWood());

		if (ToolType.SHEARS.contains(tool.getType()) && Lists.getWoodLeaves().contains(block.getType())) {
			allowed.clear();
			allowed.add(block.getType());
		}

		if (!allowed.contains(block.getType())) return;

		final int level = modManager.getModLevel(tool, this);

		final int sap_idx = block.getType().toString().lastIndexOf('_');
		final Material saplingType = Material.getMaterial(block.getType().toString().substring(0, sap_idx) + "_SAPLING");

		Bukkit.getScheduler().runTaskAsynchronously(MineTinker.getPlugin(), () -> {
			final HashSet<Block> trunkBlocks = new HashSet<>();
			final ArrayList<Block> groundBlocks = new ArrayList<>();
			if (!parseTree(block, trunkBlocks, groundBlocks, allowed) && !ToolType.SHEARS.contains(tool.getType())) return;
			final List<Block> trunkBlocksList = new ArrayList<>(trunkBlocks);
			// Sort blocks by distance to the original block (closest first) and break them in that order
			trunkBlocksList.sort(Comparator.comparingDouble(o -> (o.getLocation().distance(block.getLocation()))));
			for (final Block trunkBlock : trunkBlocksList) {
				Bukkit.getScheduler().runTask(MineTinker.getPlugin(), () -> {
					events.put(trunkBlock.getLocation(), 0);
					try {
						DataHandler.playerBreakBlock(player, trunkBlock, tool);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				});
			}

			// Place sapling on all ground blocks if applicable
			if (saplingType == null || level < 2 || groundBlocks.isEmpty()) return;
			// sort ground blocks by distance to the original block (closest first)
			groundBlocks.sort(Comparator.comparing(b -> {
				final Location l = b.getLocation();
				final Location l2 = block.getLocation();
				return (l.getX() - l2.getX()) * (l.getX() - l2.getX()) + (l.getZ() - l2.getZ()) * (l.getZ() - l2.getZ());
			}));

			// try to place saplings on all ground blocks
			Bukkit.getScheduler().runTask(MineTinker.getPlugin(), () -> {
				for (final Block groundBlock : groundBlocks) {
					for (final ItemStack stack : player.getInventory().getContents()) {
						if (stack == null) continue;
						if (stack.getType() != saplingType) continue;
						if (stack.getAmount() < 1) continue;
						if (modManager.isModifierItem(stack)) continue;

						final Block sap = groundBlock.getRelative(BlockFace.UP);
						if (!sap.getType().isAir()) break;

						final BlockPlaceEvent placeEvent =
								new BlockPlaceEvent(sap, sap.getState(), groundBlock, stack,
										player, true, EquipmentSlot.HAND);
						Bukkit.getPluginManager().callEvent(placeEvent);

						//check the pseudoevent
						if (placeEvent.canBuild() && !placeEvent.isCancelled()) {
							stack.setAmount(stack.getAmount() - 1);
							sap.setType(saplingType);
						}
						break;
					}
				}
			});
		});

		ChatWriter.logModifier(player, event, this, tool, "Block(" + block.getType() + ")");
	}

	private boolean parseTree(@NotNull Block block,
									 @NotNull final HashSet<Block> trunkBlocks, @NotNull final List<Block> groundBlocks,
							  		 @NotNull final HashSet<Material> allowed) {
		final Stack<Block> stack = new Stack<>();
		boolean hasGround = false, hasLeaves = false;
		stack.push(block);
		while (trunkBlocks.size() < maxBlocks && !stack.isEmpty()) {
			block = stack.pop();
			if (this.grasses.contains(block.getRelative(BlockFace.DOWN).getType())) {
				hasGround = true;
				groundBlocks.add(block.getRelative(BlockFace.DOWN));
			}
			if (!hasLeaves && Lists.getWoodLeaves().contains(block.getRelative(BlockFace.UP).getType()))
				hasLeaves = true;

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						Block relative = block.getRelative(dx, dy, dz);
						if (!allowed.contains(relative.getType())) continue;
						if (!trunkBlocks.add(relative)) continue; // check if visited already

						// Add block to stack if not visited yet
						stack.push(relative);
					}
				}
			}
		}

		return hasGround && hasLeaves;
	}
}
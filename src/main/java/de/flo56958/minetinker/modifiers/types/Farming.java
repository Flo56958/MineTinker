package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTBlockBreakEvent;
import de.flo56958.minetinker.api.events.MTPlayerInteractEvent;
import de.flo56958.minetinker.data.Lists;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Farming extends Modifier implements Listener {

	private static Farming instance;

	private final Map<Material, Material> materialMap = Map.of(
			Material.WHEAT, Material.WHEAT_SEEDS,
			Material.CARROTS, Material.CARROT,
			Material.POTATOES, Material.POTATO,
			Material.BEETROOTS, Material.BEETROOT_SEEDS,
			Material.NETHER_WART, Material.NETHER_WART
	);

	private int boneMealDurabilityCost;
	private int boneMealDurabilityCostReductionPerLevel;
	private boolean requireSeeds;

	private Farming() {
		super(MineTinker.getPlugin());
		customModelData = 10_067;
	}

	public static Farming instance() {
		synchronized (Farming.class) {
			if (instance == null)
				instance = new Farming();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Farming";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.HOE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);

		config.addDefault("EnchantCost", 5);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		config.addDefault("BoneMealDurabilityCost", 10);
		config.addDefault("BoneMealDurabilityCostReductionPerLevel", 2);
		config.addDefault("RequireSeeds", true);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.WHEAT);

		this.boneMealDurabilityCost = config.getInt("BoneMealDurabilityCost", 10);
		this.boneMealDurabilityCostReductionPerLevel = config.getInt("BoneMealDurabilityCostReductionPerLevel", 2);
		this.requireSeeds = config.getBoolean("RequireSeeds", true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBoneMealTry(@NotNull final MTPlayerInteractEvent event) {
		final Block block = event.getEvent().getClickedBlock();
		if (block == null) return;
		if (!(block.getBlockData() instanceof Ageable || block.getBlockData() instanceof Sapling)) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack tool = event.getTool();
		if (!modManager.hasMod(tool, this)) return;

		if (event.getEvent().getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getEvent().getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (block.getBlockData() instanceof Ageable)
				// update Blockface for Power
				Lists.BLOCKFACE.put(player, BlockFace.UP);
		}

		if (event.getEvent().getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) return;

		// check if block is bone meal-able
		block.applyBoneMeal(event.getEvent().getBlockFace());

		final int damage = boneMealDurabilityCost - (boneMealDurabilityCostReductionPerLevel * (modManager.getModLevel(tool, this) - 1));
		DataHandler.triggerItemDamage(player, tool, damage);

	}

	@EventHandler(ignoreCancelled = true)
	private void onBlockBreak(@NotNull final MTBlockBreakEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final Block block = event.getBlock();
		if (!(block.getBlockData() instanceof Ageable)) return;

		final ItemStack tool = event.getTool();
		if (!modManager.hasMod(tool, this)) return;

		final Material material = block.getType();
		final Material required = materialMap.get(material);

		if (required == null) return;

		Bukkit.getScheduler().runTaskLater(getSource(), () -> {
			if (player.getGameMode() == GameMode.CREATIVE || !requireSeeds) {
				block.setType(material);
				return;
			}

			for (ItemStack itemStack : player.getInventory().getContents()) {
				if (itemStack == null || itemStack.getType() != required)
					// This is necessary as even though this is annotated @NotNull, it's still null sometimes
					continue;

				itemStack.setAmount(itemStack.getAmount() - 1);
				block.setType(material);
			}
		}, 1L);
	}
}

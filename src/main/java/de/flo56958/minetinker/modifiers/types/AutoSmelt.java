package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTBlockBreakEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class AutoSmelt extends Modifier implements Listener {

	private static AutoSmelt instance;
	private final EnumMap<Material, @NotNull Triplet> conversions = new EnumMap<>(Material.class);

	private int percentagePerLevel;
	private boolean hasSound;
	private boolean hasParticles;
	private boolean worksUnderWater;
	private boolean toggleable;

	private AutoSmelt() {
		super(MineTinker.getPlugin());
		customModelData = 10_004;
	}

	public static AutoSmelt instance() {
		synchronized (AutoSmelt.class) {
			if (instance == null) {
				instance = new AutoSmelt();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Auto-Smelt";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.PICKAXE, ToolType.SHOVEL, ToolType.SHEARS);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 1);
		config.addDefault("PercentagePerLevel", 100);
		config.addDefault("Sound", true); //Auto-Smelt makes a sound
		config.addDefault("Particles", true); //Auto-Smelt will create a particle effect when triggered
		config.addDefault("WorksUnderWater", true);
		config.addDefault("Toggleable", false);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "CCC");
		config.addDefault("Recipe.Middle", "CFC");
		config.addDefault("Recipe.Bottom", "CCC");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("C", Material.FURNACE.name());
		recipeMaterials.put("F", Material.BLAZE_ROD.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		conversions.put(Material.STONE, new Triplet(Material.STONE, 1));
		conversions.put(Material.COBBLESTONE, new Triplet(Material.STONE, 1));
		conversions.put(Material.SAND, new Triplet(Material.GLASS, 1));
		conversions.put(Material.SNOW, new Triplet(Material.AIR, 0));
		conversions.put(Material.SNOW_BLOCK, new Triplet(Material.AIR, 0));
		conversions.put(Material.RED_SAND, new Triplet(Material.RED_STAINED_GLASS, 1));
		conversions.put(Material.WHITE_TERRACOTTA, new Triplet(Material.WHITE_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.ORANGE_TERRACOTTA, new Triplet(Material.ORANGE_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.MAGENTA_TERRACOTTA, new Triplet(Material.MAGENTA_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.LIGHT_BLUE_TERRACOTTA, new Triplet(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.YELLOW_TERRACOTTA, new Triplet(Material.YELLOW_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.LIME_TERRACOTTA, new Triplet(Material.LIME_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.PINK_TERRACOTTA, new Triplet(Material.PINK_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.GRAY_TERRACOTTA, new Triplet(Material.GRAY_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.LIGHT_GRAY_TERRACOTTA, new Triplet(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.CYAN_TERRACOTTA, new Triplet(Material.CYAN_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.PURPLE_TERRACOTTA, new Triplet(Material.PURPLE_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.BLUE_TERRACOTTA, new Triplet(Material.BLUE_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.BROWN_TERRACOTTA, new Triplet(Material.BROWN_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.GREEN_TERRACOTTA, new Triplet(Material.GREEN_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.RED_TERRACOTTA, new Triplet(Material.RED_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.BLACK_TERRACOTTA, new Triplet(Material.BLACK_GLAZED_TERRACOTTA, 1));
		conversions.put(Material.ACACIA_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.BIRCH_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.DARK_OAK_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.JUNGLE_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.OAK_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.SPRUCE_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_ACACIA_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_BIRCH_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_DARK_OAK_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_JUNGLE_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_OAK_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_SPRUCE_LOG, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.ACACIA_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.BIRCH_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.DARK_OAK_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.JUNGLE_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.OAK_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.SPRUCE_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_ACACIA_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_BIRCH_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_DARK_OAK_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_JUNGLE_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_OAK_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.STRIPPED_SPRUCE_WOOD, new Triplet(Material.CHARCOAL, 1));
		conversions.put(Material.ACACIA_LEAVES, new Triplet(Material.STICK, 1));
		conversions.put(Material.BIRCH_LEAVES, new Triplet(Material.STICK, 1));
		conversions.put(Material.DARK_OAK_LEAVES, new Triplet(Material.STICK, 1));
		conversions.put(Material.JUNGLE_LEAVES, new Triplet(Material.STICK, 1));
		conversions.put(Material.OAK_LEAVES, new Triplet(Material.STICK, 1));
		conversions.put(Material.SPRUCE_LEAVES, new Triplet(Material.STICK, 1));

		conversions.put(Material.IRON_ORE, new Triplet(Material.IRON_INGOT, 1, true));
		conversions.put(Material.GOLD_ORE, new Triplet(Material.GOLD_INGOT, 1, true));
		conversions.put(Material.NETHERRACK, new Triplet(Material.NETHER_BRICK, 1, true));
		conversions.put(Material.KELP_PLANT, new Triplet(Material.DRIED_KELP, 1));
		conversions.put(Material.WET_SPONGE, new Triplet(Material.SPONGE, 1));
		conversions.put(Material.COAL_ORE, new Triplet(Material.AIR, 0));
		conversions.put(Material.COAL_BLOCK, new Triplet(Material.AIR, 0));
		conversions.put(Material.CLAY, new Triplet(Material.BRICK, 4, true));

		if (MineTinker.is16compatible) {
			conversions.put(Material.ANCIENT_DEBRIS, new Triplet(Material.NETHERITE_SCRAP, 2, true));
			conversions.put(Material.NETHER_GOLD_ORE, new Triplet(Material.GOLD_INGOT, 1, true));
			conversions.put(Material.CRIMSON_HYPHAE, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.CRIMSON_STEM, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.WARPED_HYPHAE, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.WARPED_STEM, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.STRIPPED_CRIMSON_HYPHAE, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.STRIPPED_CRIMSON_STEM, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.STRIPPED_WARPED_HYPHAE, new Triplet(Material.CHARCOAL, 1));
			conversions.put(Material.STRIPPED_WARPED_STEM, new Triplet(Material.CHARCOAL, 1));
		}

		if (MineTinker.is17compatible) {
			conversions.put(Material.AZALEA_LEAVES, new Triplet(Material.STICK, 1));

			conversions.put(Material.RAW_COPPER_BLOCK, new Triplet(Material.COPPER_INGOT, 9));
			conversions.put(Material.RAW_GOLD_BLOCK, new Triplet(Material.GOLD_INGOT, 9));
			conversions.put(Material.RAW_IRON_BLOCK, new Triplet(Material.IRON_INGOT, 9));
			conversions.put(Material.COPPER_ORE, new Triplet(Material.COPPER_INGOT, 1, true));
		}

		//Saving Conversions as String
		Map<String, String> conversionsSTR = new HashMap<>();
		conversions.forEach((k, v) -> conversionsSTR.put(k.toString(), v.toString()));
		config.addDefault("Conversions", conversionsSTR);
		conversions.clear();

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.FURNACE);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 20);
		this.hasSound = config.getBoolean("Sound", true);
		this.hasParticles = config.getBoolean("Particles", true);
		this.worksUnderWater = config.getBoolean("WorksUnderWater", true);
		this.toggleable = config.getBoolean("Toggleable", false);

		ConfigurationSection conversionConfig = config.getConfigurationSection("Conversions");
		if (conversionConfig == null) return;

		Map<String, Object> conversionValues = conversionConfig.getValues(false);
		conversionValues.forEach((k, v) -> {
			Material material = Material.getMaterial(k);
			if (material != null && v instanceof String) conversions.put(material, Objects.requireNonNull(Triplet.fromString((String) v)));
		});

		this.description = this.description.replace("%chance", String.valueOf(this.percentagePerLevel));
	}

	/**
	 * The Effect for the BlockBreak-Listener
	 *
	 * @param event the Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(@NotNull MTBlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		Block block = event.getBlock();
		BlockBreakEvent breakEvent = event.getEvent();

		if (!player.hasPermission("minetinker.modifiers.autosmelt.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (!worksUnderWater) {
			if (player.isSwimming() || player.getWorld().getBlockAt(player.getLocation()).getType() == Material.WATER)
				return;
		}

		if (toggleable) {
			if (player.isSneaking()) return;
		}

		if (!(new Random().nextInt(100) <= this.percentagePerLevel * modManager.getModLevel(tool, this)
				&& block.getLocation().getWorld() != null)) return;

		Triplet conv = conversions.get(block.getType());
		if (conv == null) return;

		int amount = conv.amount;
		Material loot = conv.material;
		if (loot == null) return;

		if (conv.luckable) {
			int level = modManager.getModLevel(tool, Luck.instance());

			if (level > 0) amount = amount + new Random().nextInt(level + 1) * amount;
			//Times amount is for clay as it drops 4 per block
		}

		if (!(loot == Material.AIR || amount <= 0)) {
			ItemStack items = new ItemStack(loot, amount);

			if (modManager.hasMod(tool, Directing.instance())) {
				if (player.getInventory().addItem(items).size() != 0) { //adds items to (full) inventory
					player.getWorld().dropItem(player.getLocation(), items);
				} // no else as it gets added in if-clause
			} else {
				block.getLocation().getWorld().dropItemNaturally(block.getLocation(), items);
			}
		}

		breakEvent.setDropItems(false);

		if (this.hasParticles) block.getLocation().getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 5);

		if (this.hasSound) block.getLocation().getWorld().playSound(block.getLocation(),
				Sound.ENTITY_GENERIC_BURN, 0.2F, 0.5F);

		ChatWriter.logModifier(player, event, this, tool, "Block(" + breakEvent.getBlock().getType().toString() + ")");
	}

	private static class Triplet {
		private static final String regex = ":";

		final int amount;
		final Material material;
		final boolean luckable;

		@Contract(pure = true)
		private Triplet(Material m, int amount) {
			this.amount = amount;
			this.material = m;
			this.luckable = false;
		}

		@Contract(pure = true)
		private Triplet(Material m, int amount, boolean luckable) {
			this.amount = amount;
			this.material = m;
			this.luckable = luckable;
		}

		@Nullable
		static Triplet fromString(@NotNull String input) {
			String[] tok = input.split(regex);
			try {
				if (tok.length == 2) {
					return new Triplet(Material.valueOf(tok[0]), Integer.parseInt(tok[1]));
				} else if (tok.length == 3) {
					return new Triplet(Material.valueOf(tok[0]), Integer.parseInt(tok[1]), Boolean.parseBoolean(tok[2]));
				} else {
					return null;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			}
		}

		@NotNull
		public String toString() {
			return material.toString() + regex + amount + regex + luckable;
		}
	}
}

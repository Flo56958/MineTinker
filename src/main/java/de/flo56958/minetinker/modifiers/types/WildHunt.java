package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDeathEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class WildHunt extends Modifier implements Listener {

	private static WildHunt instance;
	private final HashMap<EntityType, @NotNull List<Tupel>> conversions = new HashMap<>();

	private int percentagePerLevel;

	private WildHunt() {
		super(MineTinker.getPlugin());
		customModelData = 10_062;
	}

	public static WildHunt instance() {
		synchronized (WildHunt.class) {
			if (instance == null) {
				instance = new WildHunt();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Wild-Hunt";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.SWORD, ToolType.BOW, ToolType.CROSSBOW);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GOLD%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("PercentagePerLevel", 10);

		config.addDefault("EnchantCost", 15);
		config.addDefault("Enchantable", true);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", false);

		//Saving Conversions as String
		conversions.clear();

		conversions.put(EntityType.ENDERMAN, List.of(new Tupel(Material.ENDER_EYE, Material.ENDER_PEARL)));
		conversions.put(EntityType.ZOMBIE, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.ZOMBIE_HORSE, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.ZOMBIE_VILLAGER, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.DROWNED, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.HUSK, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.ZOGLIN, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH)));
		conversions.put(EntityType.ZOMBIFIED_PIGLIN, List.of(new Tupel(Material.LEATHER, Material.ROTTEN_FLESH),
																new Tupel(Material.GOLD_INGOT, Material.GOLD_NUGGET)));
		conversions.put(EntityType.SPIDER, List.of(new Tupel(Material.COBWEB, Material.STRING)));
		conversions.put(EntityType.CREEPER, List.of(new Tupel(Material.TNT, Material.GUNPOWDER)));
		conversions.put(EntityType.GHAST, List.of(new Tupel(Material.TNT, Material.GUNPOWDER)));
		conversions.put(EntityType.WITHER_SKELETON, List.of(new Tupel(Material.WITHER_ROSE, Material.COAL)));
		conversions.put(EntityType.SHULKER, List.of(new Tupel(Material.SHULKER_BOX, Material.SHULKER_SHELL)));
		conversions.put(EntityType.SLIME, List.of(new Tupel(Material.EMERALD, Material.SLIME_BALL)));

		conversions.forEach((k, v) -> config.addDefault("Conversions." + k.toString(), v.stream().map(Tupel::toString).toList()));
		conversions.clear();

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.BONE);

		this.percentagePerLevel = config.getInt("PercentagePerLevel", 10);
		this.description = this.description.replace("%chance", String.valueOf(this.percentagePerLevel));

		for (EntityType entity : EntityType.values()) {
			List<String> strings = config.getStringList("Conversions." + entity.toString());
			if (strings.isEmpty()) continue;

			HashSet<Tupel> set = new HashSet<>(strings.stream().map(Tupel::fromString).toList());
			set.remove(null);
			if (set.isEmpty()) continue;
			conversions.put(entity, new LinkedList<>(set));
		}
	}

	/**
	 * The Effect for the BlockBreak-Listener
	 *
	 * @param event the Event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void effect(@NotNull MTEntityDeathEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.wildhunt.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		final Random rand = new Random();

		final List<Tupel> conv = conversions.get(event.getEvent().getEntity().getType());
		if (conv == null) return;

		final List<ItemStack> drops = event.getEvent().getDrops();
		for (Tupel q : conv) {
			Material loot = q.material;
			Material replaces = q.replaces;
			if (loot == null || replaces == null) continue;

			LinkedList<ItemStack> items = new LinkedList<>(drops);
			items.removeIf(k -> k.getType() != replaces);

			if (items.isEmpty()) continue;

			int amount = 0;
			for (ItemStack item : items) {
				amount += item.getAmount();
			}
			if (amount <= 0) continue;

			int convertedAmount = 0;

			for (int i = 0, a = amount; i < a; i++) {
				if (!(rand.nextInt(100) <= this.percentagePerLevel * modManager.getModLevel(tool, this)))
					continue;
				amount--;
				convertedAmount++;
			}

			if (convertedAmount > 0) {
				drops.removeAll(items);
				drops.add(new ItemStack(loot, convertedAmount));
				if (amount > 0) drops.add(new ItemStack(replaces, amount));
				//Track stats
				int stat = (DataHandler.hasTag(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, false))
						? DataHandler.getTag(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, false)
						: 0;
				DataHandler.setTag(tool, getKey() + "_stat_used", stat + convertedAmount, PersistentDataType.INTEGER, false);
				ChatWriter.logModifier(player, event, this, tool, "Entity(" + event.getEvent().getEntity().getType() + ")",
						"Item(" + replaces + "->" + loot + ")");
			}
		}
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		List<String> lore = new ArrayList<>();
		int stat = (DataHandler.hasTag(item, getKey() + "_stat_used", PersistentDataType.INTEGER, false))
				? DataHandler.getTag(item, getKey() + "_stat_used", PersistentDataType.INTEGER, false)
				: 0;
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Wild-Hunt.Statistic_Used")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}

	private static class Tupel {
		private static final String regex = ":";

		final Material material;
		final Material replaces;

		@Contract(pure = true)
		private Tupel(final Material m, final Material replaces) {
			this.material = m;
			this.replaces = replaces;
		}

		@Nullable
		static WildHunt.Tupel fromString(@NotNull String input) {
			String[] tok = input.split(regex);
			try {
				if (tok.length == 2) {
					return new Tupel(Material.valueOf(tok[0]), Material.valueOf(tok[1]));
				} else {
					return null;
				}
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}

		@NotNull
		public String toString() {
			return material.toString() + regex + replaces;
		}
	}
}

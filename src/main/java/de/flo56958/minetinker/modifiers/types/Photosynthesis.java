package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Photosynthesis extends Modifier {

	private static Photosynthesis instance;
	private final ConcurrentHashMap<Player, Tupel> data = new ConcurrentHashMap<>();
	private final HashSet<Material> allowedMaterials = new HashSet<>();
	private int healthRepair;
	private int taskID = -1;
	private int tickTime;
	private double multiplierPerTick;
	private boolean fullEffectAtNoon;
	private boolean allowOffhand;
	private boolean mustStandStill;
	private boolean notifyWhenActive;
	private final Runnable runnable = () -> {
		final long time = System.currentTimeMillis();
		data.entrySet().removeIf(entry -> entry.getKey() == null || !entry.getKey().isOnline());

		for (final Player player : Bukkit.getOnlinePlayers()) {
			if (player == null) continue;
			if (!player.isOnline()) {
				data.remove(player);
				continue;
			}
			if (!player.hasPermission(getUsePermission())) continue;

			final PlayerInventory inv = player.getInventory();
			final ArrayList<ItemStack> items = new ArrayList<>(6);
            Collections.addAll(items, inv.getArmorContents());
			items.add(inv.getItemInMainHand());
			if (allowOffhand) items.add(inv.getItemInOffHand());
			items.removeIf(item -> !modManager.isToolViable(item) && !modManager.isArmorViable(item));
			items.removeIf(item -> !modManager.hasMod(item, this));
			if (items.isEmpty()) { // no photosynthesis items
				data.remove(player);
				continue;
			}

			final Tupel tupel = data.getOrDefault(player, new Tupel(player.getLocation(), System.currentTimeMillis(), false));
			data.put(player, tupel);

			final Location pLoc = player.getLocation();
			if (mustStandStill && !pLoc.equals(tupel.loc)) {
				tupel.loc = pLoc;
				tupel.time = time;
				continue;
			}

			tupel.isAboveGround = false; // rechecking is required as world could have changed
			tupel.loc = pLoc;

			if (pLoc.getWorld().getEnvironment() == World.Environment.NORMAL) { //check for overworld
				final Block start = pLoc.getBlock();
				for (int i = 1; i < pLoc.getWorld().getMaxHeight() - pLoc.getBlockY(); i++) {
					final Block b = start.getRelative(0, i, 0);
					if (!b.isPassable() && !allowedMaterials.contains(b.getType())) {
						tupel.isAboveGround = false;
						break;
					}
					tupel.isAboveGround = true;
				}
			}

			final long worldTime = tupel.loc.getWorld().getTime() / 1000; //to get hours; 0 -> 6am

			if (!tupel.isAboveGround || tupel.loc.getWorld().hasStorm() || worldTime > 12) {
                // reset time
                tupel.time = time;
				continue;
            }

            final double daytimeMultiplier = (fullEffectAtNoon) ? Math.abs(6 - worldTime) / 6.0 : 1.0;
            final long timeDif = time - tupel.time - (tickTime * 50L); //to make effect faster with time (first tick period does not count)
			final double timeAdvantage = multiplierPerTick * ((timeDif / 50.0) / tickTime);

			boolean triggered = false;
            for (final ItemStack item : items) {
                final int level = modManager.getModLevel(item, this);

				if (!(item.getItemMeta() instanceof Damageable meta)) continue;

				final int oldDamage = meta.getDamage();
				if (oldDamage <= 0) continue; //no repair needed

				final int repair = (int) Math.round(healthRepair * timeAdvantage * level * daytimeMultiplier);
				if (repair <= 0) continue;

				final int newDamage = Math.max(0, oldDamage - repair);

				ChatWriter.logModifier(player, null, this, item,
						String.format("ItemDamage(%d -> %d [%d])", oldDamage, newDamage, repair),
						"DaytimeMultiplier(" + daytimeMultiplier + ")",
						String.format("TimeAdvantage(%.2f * (%d / %d) = %.2f)", multiplierPerTick, timeDif / 50,
								tickTime, timeAdvantage));

				meta.setDamage(newDamage);
				item.setItemMeta(meta);

				// Track statistic
				int stat = DataHandler.getTagOrDefault(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, 0);
				stat += oldDamage - newDamage;
				DataHandler.setTag(item, getKey() + "_stat_healed", stat, PersistentDataType.INTEGER);

				triggered = true;
			}

			if (triggered && notifyWhenActive)
				ChatWriter.sendActionBar(player, this.getColor()
						+ LanguageManager.getString("Modifier.Photosynthesis.NotifyWhenActive", player));
		}
	};

	private Photosynthesis() {
		super(MineTinker.getPlugin());
		customModelData = 10_024;
	}

	public static Photosynthesis instance() {
		synchronized (Photosynthesis.class) {
			if (instance == null)
				instance = new Photosynthesis();
		}

		return instance;
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		final List<String> lore = new ArrayList<>();
		final int stat = DataHandler.getTagOrDefault(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, 0);
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Photosynthesis.Statistic_Healed")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}

	@Override
	public String getKey() {
		return "Photosynthesis";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.ALL);
	}

	@Override
	public void reload() {
		if (taskID != -1) Bukkit.getScheduler().cancelTask(taskID);

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.VINE.name());
		config.addDefault("HealthRepairPerLevel", 1); //per Tick
		config.addDefault("MultiplierPerTick", 1.05);
		config.addDefault("TickTime", 100); //TickTime in Minecraft ticks
		config.addDefault("FullEffectAtNoon", true); //if false: full effect always in daylight
		config.addDefault("AllowOffHand", true); //if false: only main hand
		config.addDefault("MustStandStill", false); //if true: Players need to stand still
		config.addDefault("NotifyWhenActive", false); //Notifies the Player via Actionbar

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "DGD");
		config.addDefault("Recipe.Middle", "GVG");
		config.addDefault("Recipe.Bottom", "DGD");

		HashMap<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DAYLIGHT_DETECTOR.name());
		recipeMaterials.put("G", Material.GRASS_BLOCK.name());
		recipeMaterials.put("V", Material.VINE.name());
		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.healthRepair = config.getInt("HealthRepairPerLevel", 2);
		this.tickTime = config.getInt("TickTime", 100);
		this.multiplierPerTick = config.getDouble("MultiplierPerTick", 1.05);
		this.fullEffectAtNoon = config.getBoolean("FullEffectAtNoon", true);
		this.allowOffhand = config.getBoolean("AllowOffHand", true);
		this.mustStandStill = config.getBoolean("MustStandStill", false);
		this.notifyWhenActive = config.getBoolean("NotifyWhenActive", false);

		this.description = this.description
				.replace("%amount", String.valueOf(healthRepair))
				.replace("%ticks", String.valueOf(tickTime))
				.replace("%multiplier", String.valueOf(Math.round((multiplierPerTick - 1.0) * 100)));

		allowedMaterials.clear();
		allowedMaterials.addAll(Arrays.stream(Material.values())
						.filter(mat -> mat.isAir() || !mat.isOccluding() || !mat.isSolid()).toList());

		if (isAllowed())
			this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.getSource(), this.runnable, 5 * 20L, this.tickTime);
		else
			this.taskID = -1;
	}

	//------------------------------------------------------

	private static class Tupel {
		private Location loc;
		private long time; //in ms
		private boolean isAboveGround; //isAboveGround is not always false
		private Tupel(@NotNull Location loc, long time, boolean isAboveGround) {
			this.loc = loc;
			this.time = time;
			this.isAboveGround = isAboveGround;
		}
	}
}

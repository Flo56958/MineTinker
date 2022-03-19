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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Photosynthesis extends Modifier implements Listener {

	private static Photosynthesis instance;
	private int healthRepair;
	private int taskID = -1;
	private final ConcurrentHashMap<UUID, Tupel> data = new ConcurrentHashMap<>();
	private int tickTime;
	private double multiplierPerTick;
	private boolean fullEffectAtNoon;
	private boolean allowOffhand;
	private boolean mustStandStill;
	private boolean notifyWhenActive;

	private List<Material> allowedMaterials = new ArrayList<>();

	private final Runnable runnable = () -> {
		for (final UUID id : data.keySet()) {
			final Player player = Bukkit.getPlayer(id);
			if (player == null || !player.isOnline()) {
				data.remove(id);
				continue;
			}

			if (player.isDead() || player.isFlying() || player.isGliding() || player.isSleeping() || player.isSwimming()) continue;
			if (!player.hasPermission("minetinker.modifiers.photosynthesis.use")) continue;

			final Tupel tupel = data.get(id);
			final Location pLoc = player.getLocation();

			boolean isAboveGround = false;
			if (pLoc.getWorld().getEnvironment() == World.Environment.NORMAL) { //check for overworld
				final int maxHeight = (MineTinker.is17compatible) ? 320 : 256;
				for (int i = pLoc.getBlockY() + 1; i <= maxHeight; i++) {
					Block b = pLoc.getWorld().getBlockAt(pLoc.getBlockX(), i, pLoc.getBlockZ());
					if (!(allowedMaterials.contains(b.getType()))) {
						isAboveGround = false;
						break;
					}
					isAboveGround = true;
				}
			}
			tupel.isAboveGround = isAboveGround;

			if (tupel.loc == null) {
				tupel.loc = pLoc;
			}

			if (isAboveGround) {
				if (mustStandStill && !(player.getWorld().equals(tupel.loc.getWorld()) && pLoc.getX() == tupel.loc.getX()
						&& pLoc.getY() == tupel.loc.getY() && pLoc.getZ() == tupel.loc.getZ())) {
					tupel.time = System.currentTimeMillis(); //reset time
					tupel.loc = pLoc; //update Position
					continue; //does not work while raining
				}
				if (tupel.loc.getWorld().hasStorm()) {
					tupel.time = System.currentTimeMillis(); //reset time
					continue; //does not work while raining
				}

				final long worldTime = tupel.loc.getWorld().getTime() / 1000; //to get hours; 0 -> 6am
				if (worldTime > 12) { //after 6pm
					tupel.time = System.currentTimeMillis(); //reset time
					continue; //does not work while night
				}

				double daytimeMultiplier = 1.0;
				if (fullEffectAtNoon) {
					final long difference = Math.abs(6 - worldTime);
					daytimeMultiplier = (6 - difference) / 6.0; //value range: 0.0 - 1.0
				}

				long timeDif = System.currentTimeMillis() - tupel.time - (tickTime * 50L); //to make effect faster with time (first tick period does not count)
				if (!tupel.isAboveGround) continue;

				final PlayerInventory inv = player.getInventory();
				final ItemStack[] items = new ItemStack[6];

				int i = 0;
				for (final ItemStack item : inv.getArmorContents()) {
					items[i++] = item;
				}

				items[4] = inv.getItemInMainHand();
				if (allowOffhand) items[5] = inv.getItemInOffHand();

				final double timeAdvantage = multiplierPerTick * ((timeDif / 50.0) / tickTime);

				for (final ItemStack item : items) {
					if (item == null) continue;
					if (!(modManager.isToolViable(item) || modManager.isArmorViable(item))) continue;
					//is MineTinker at this point

					final int level = modManager.getModLevel(item, this);
					if (level <= 0) continue; //does not have the mod

					final ItemMeta meta = item.getItemMeta();
					if (meta instanceof Damageable) {
						final int oldDamage = ((Damageable) meta).getDamage();
						if (oldDamage == 0) continue; //no repair needed

						final int repair = (int) Math.round(healthRepair * timeAdvantage * level * daytimeMultiplier);
						int newDamage = oldDamage - repair;

						if (newDamage < 0) newDamage = 0;
						if (notifyWhenActive)
							ChatWriter.sendActionBar(player,
									this.getColor()
											+ LanguageManager.getString("Modifier.Photosynthesis.NotifyWhenActive", player));

						ChatWriter.logModifier(player, null, this, item,
								String.format("ItemDamage(%d -> %d [%d])", oldDamage, newDamage, repair),
								"DaytimeMultiplier(" + daytimeMultiplier + ")",
								String.format("TimeAdvantage(%.2f * (%d / %d) = %.2f)", multiplierPerTick, timeDif / 50,
										tickTime, timeAdvantage));

						((Damageable) meta).setDamage(newDamage);
						item.setItemMeta(meta);

						// Track statistic
						int stat = (DataHandler.hasTag(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, false))
								? DataHandler.getTag(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, false)
								: 0;
						stat += oldDamage - newDamage;
						DataHandler.setTag(item, getKey() + "_stat_healed", stat,PersistentDataType.INTEGER, false);
					}
				}
			} else {
				tupel.time = System.currentTimeMillis();
				tupel.loc = pLoc;
			}
		}
	};

	@Override
	public List<String> getStatistics(ItemStack item) {
		int stat = (DataHandler.hasTag(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, false))
				? DataHandler.getTag(item, getKey() + "_stat_healed", PersistentDataType.INTEGER, false)
				: 0;
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Photosynthesis.Statistic_Healed")
				.replaceAll("%amount", String.valueOf(stat)));
		return lore;
	}

	private Photosynthesis() {
		super(MineTinker.getPlugin());
		customModelData = 10_024;
	}

	public static Photosynthesis instance() {
		synchronized (Photosynthesis.class) {
			if (instance == null) {
				instance = new Photosynthesis();
			}
		}

		return instance;
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
		if (taskID != -1) {
			Bukkit.getScheduler().cancelTask(taskID);
		}

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
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

		init(Material.VINE);

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
		allowedMaterials.add(Material.AIR);
		allowedMaterials.add(Material.CAVE_AIR);
		allowedMaterials.add(Material.VOID_AIR);
		allowedMaterials.add(Material.GLASS);
		allowedMaterials.add(Material.BARRIER);
		allowedMaterials.add(Material.TALL_GRASS);
		allowedMaterials.add(Material.DEAD_BUSH);
		if (MineTinker.is17compatible) {
			allowedMaterials.add(Material.LIGHT);
		}

		if (isAllowed()) {
			data.clear();

			for (final Player player : Bukkit.getOnlinePlayers()) {
				data.putIfAbsent(player.getUniqueId(), new Tupel(player.getLocation(), System.currentTimeMillis(), false));
			}
			this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineTinker.getPlugin(), this.runnable, 5 * 20L, this.tickTime);
		} else {
			this.taskID = -1;
		}
	}

	//------------------------------------------------------

	@EventHandler
	public void onJoin(@NotNull final PlayerJoinEvent event) {
		data.putIfAbsent(event.getPlayer().getUniqueId(), new Tupel(event.getPlayer().getLocation(), System.currentTimeMillis(), false));
	}

	@EventHandler
	public void onQuit(@NotNull final PlayerQuitEvent event) {
		data.remove(event.getPlayer().getUniqueId());
	}

	private static class Tupel {
		private Location loc;
		private long time; //in ms
		private boolean isAboveGround; //isAboveGround is not always false
		private Tupel(Location loc, long time, @SuppressWarnings("SameParameterValue") boolean isAboveGround) {
			this.loc = loc;
			this.time = time;
			this.isAboveGround = isAboveGround;
		}
	}
}

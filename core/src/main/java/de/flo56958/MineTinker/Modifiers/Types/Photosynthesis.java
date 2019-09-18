package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Photosynthesis extends Modifier implements Listener {

	private static Photosynthesis instance;
	private int healthRepair;
	private int taskID = -1;
	private ConcurrentHashMap<UUID, Tupel> data = new ConcurrentHashMap<>();
	private int tickTime;
	private double multiplierPerTick;
	private boolean fullEffectAtNoon;

	private Runnable runnable = () -> {
		for (UUID id : data.keySet()) {
			Player p = Bukkit.getPlayer(id);
			if (p == null || !p.isOnline()) {
				data.remove(id);
				continue;
			}

			if (p.isDead()) continue;

			Tupel t = data.get(id);
			Location pLoc = p.getLocation();
			if (pLoc.getWorld().equals(t.loc.getWorld()) && pLoc.getX() == t.loc.getX() && pLoc.getY() == t.loc.getY() && pLoc.getZ() == t.loc.getZ()) {
				if (t.loc.getWorld().hasStorm()) {
					t.time = System.currentTimeMillis(); //reset time
					continue; //does not work while raining
				}
				long worldTime = t.loc.getWorld().getTime() / 1000; //to get hours; 0 -> 6am
				if (worldTime > 12) { //after 6pm
					t.time = System.currentTimeMillis(); //reset time
					continue; //does not work while night
				}
				double daytimeMultiplier = 1.0;
				if (fullEffectAtNoon) {
					long difference = Math.abs(6 - worldTime);
					daytimeMultiplier = 1.0 - (10 - difference) / 10.0; //value range: 0.4 - 1.0
				}

				long timeDif = System.currentTimeMillis() - t.time - (tickTime * 50); //to make effect faster with time (first tick period does not count)
				if (!t.isAboveGround) continue;

				PlayerInventory inv = p.getInventory();
				ItemStack[] items = new ItemStack[6];
				int i = 0;
				for(ItemStack item : inv.getArmorContents()) {
					items[i++] = item;
				}
				items[4] = inv.getItemInMainHand();
				items[5] = inv.getItemInOffHand();

				double timeAdvantage = multiplierPerTick * ((timeDif / 50) / tickTime);

				for (ItemStack item : items) {
					if (item == null) continue;
					if (!(modManager.isToolViable(item) || modManager.isArmorViable(item))) continue;
					//is MineTinker at this point

					int level = modManager.getModLevel(item, this);
					if (level <= 0) continue; //does not have the mod

					ItemMeta meta = item.getItemMeta();
					if (meta instanceof Damageable) {
						int damage = ((Damageable) meta).getDamage();
						if (damage == 0) continue; //no repair needed

						damage -= healthRepair * timeAdvantage * level * daytimeMultiplier;

						if (damage < 0) damage = 0;

						((Damageable) meta).setDamage(damage);
						item.setItemMeta(meta);
					}
				}
			} else {
				t.loc = p.getLocation(); //update location
				t.time = System.currentTimeMillis();

				boolean isAboveGround = false;
				if (t.loc.getWorld().getEnvironment().getId() == 0) { //check for overworld
					for(int i = t.loc.getBlockY(); i <= 256; i++) {
						Block b = t.loc.getWorld().getBlockAt(t.loc.getBlockX(), i, t.loc.getBlockZ());
						if (!(b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR || b.getType() == Material.VOID_AIR
								|| b.getType() == Material.GLASS || b.getType() == Material.BARRIER)) {
							isAboveGround = false;
							break;
						}
						isAboveGround = true;
					}
				}

				t.isAboveGround = isAboveGround;
			}
		}
	};

	//location, time since started

	private Photosynthesis() {
		super(Main.getPlugin());

		Bukkit.getPluginManager().registerEvents(this, Main.getPlugin());
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
		config.addDefault("Name", "Photosynthesis");
		config.addDefault("ModifierItemName", "Extracted Chlorophyll");
		config.addDefault("Description", "Repair your item with %amount durability per level every %ticks Ticks when standing still while the sun shines. The longer you stand still the faster it repairs. (%multiplier%)");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Photosynthesis-Modifier");
		config.addDefault("Color", "%GREEN%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("HealthRepairPerLevel", 2); //per Tick
		config.addDefault("MultiplierPerTick", 1.05);
		config.addDefault("TickTime", 100); //TickTime in Minecraft ticks
		config.addDefault("FullEffectAtNoon", true); //if false: full effect always in daylight

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "DGD");
		config.addDefault("Recipe.Middle", "GVG");
		config.addDefault("Recipe.Bottom", "DGD");

		HashMap<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DAYLIGHT_DETECTOR.name());
		recipeMaterials.put("G", Material.GRASS_BLOCK.name());
		recipeMaterials.put("V", Material.VINE.name());
		config.addDefault("Recipe.Materials", recipeMaterials);

		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.VINE, true);

		this.healthRepair = config.getInt("HealthRepairPerLevel", 2);
		this.tickTime = config.getInt("TickTime", 100);
		this.multiplierPerTick = config.getDouble("MultiplierPerTick", 1.05);
		this.fullEffectAtNoon = config.getBoolean("FullEffectAtNoon", true);

		this.description = this.description
				.replace("%amount", String.valueOf(healthRepair))
				.replace("%ticks", String.valueOf(tickTime))
				.replace("%multiplier", String.valueOf(Math.round((multiplierPerTick - 1.0) * 100)));

		if (isAllowed()) {
			data.clear();
			for (Player p : Bukkit.getOnlinePlayers()) {
				data.putIfAbsent(p.getUniqueId(), new Tupel(p.getLocation(), System.currentTimeMillis(), false));
			}
			this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this.runnable, 5 * 20L, this.tickTime);
		} else {
			this.taskID = -1;
		}
	}

	//------------------------------------------------------

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		data.putIfAbsent(e.getPlayer().getUniqueId(), new Tupel(e.getPlayer().getLocation(), System.currentTimeMillis(), false));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		data.remove(e.getPlayer().getUniqueId());
	}

	private static class Tupel {
		private Location loc;
		private long time; //in ms
		private boolean isAboveGround;

		private Tupel(Location loc, long time, boolean isAboveGround) {
			this.loc = loc;
			this.time = time;
			this.isAboveGround = isAboveGround;
		}
	}
}

package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class ShadowDive extends Modifier implements Listener {

	private static ShadowDive instance;

	private int requiredLightLevel;
	private final HashSet<Player> activePlayers = new HashSet<>();

	private BukkitTask task;
	private ShadowDive() {
		super(MineTinker.getPlugin());
		customModelData = 10_052;
	}

	public static ShadowDive instance() {
		synchronized (ShadowDive.class) {
			if (instance == null) {
				instance = new ShadowDive();
			}
		}

		return instance;
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Iterator<Player> iterator = activePlayers.iterator();
			while (iterator.hasNext()) {
				Player p = iterator.next();
				Location loc = p.getLocation();
				byte lightlevel = p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getLightLevel();
				if (lightlevel > requiredLightLevel || p.hasPotionEffect(PotionEffectType.GLOWING)) {
					showPlayer(p);
					ChatWriter.sendActionBar(p, ChatColor.RED + ShadowDive.instance().getName() + ": "
							+ LanguageManager.getString("Modifier.Shadow-Dive.LightToHigh", p));
				} else if (PlayerInfo.isCombatTagged(p)) {
					showPlayer(p);
					ChatWriter.sendActionBar(p, ChatColor.RED + ShadowDive.instance().getName() + ": "
							+ LanguageManager.getString("Modifier.Shadow-Dive.InCombat", p));
				} else {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						if (pl.equals(p)) continue;
						if (pl.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
							pl.showPlayer(MineTinker.getPlugin(), p);
						} else {
							pl.hidePlayer(MineTinker.getPlugin(), p);
						}
					}
				}
			}
		}
	};

	@Override
	public String getKey() {
		return "Shadow-Dive";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.BOOTS);
	}

	@Override
	public void reload() {
		if (task != null) {
			task.cancel();
			task = null;
		}
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%GRAY%");
		config.addDefault("MaxLevel", 1);
		config.addDefault("SlotCost", 3);
		config.addDefault("RequiredLightLevel", 6);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "CDC");
		config.addDefault("Recipe.Middle", "DDD");
		config.addDefault("Recipe.Bottom", "CDC");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("D", Material.DIAMOND.name());
		recipeMaterials.put("C", Material.COBWEB.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.DIAMOND);
		this.requiredLightLevel = config.getInt("RequiredLightLevel", 6);

		this.description = this.description.replaceAll("%level", String.valueOf(this.requiredLightLevel));

		if (this.isAllowed()) task = Bukkit.getScheduler().runTaskTimer(MineTinker.getPlugin(), runnable, 0,5);
	}

	private void hidePlayer(Player p) {
		activePlayers.add(p);

		//Clear all mob targets
		Collection<Entity> nearbyEntities = p.getWorld().getNearbyEntities(p.getLocation(), 64, 64, 64);
		for (Entity ent : nearbyEntities) {
			if (ent instanceof Creature) {
				if (p.equals(((Creature) ent).getTarget())) {
					((Creature) ent).setTarget(null);
				}
			}
		}

		//Hide from all players
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!p.equals(player)) {
				if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) player.hidePlayer(MineTinker.getPlugin(), p);
			}
		}
	}

	private void showPlayer(Player p) {
		if (activePlayers.remove(p)) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (!p.equals(player)) player.showPlayer(MineTinker.getPlugin(), p);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		for(Player p : activePlayers) {
			event.getPlayer().hidePlayer(MineTinker.getPlugin(), p);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onLeave(PlayerQuitEvent event) {
		for(Player p : activePlayers) {
			event.getPlayer().showPlayer(MineTinker.getPlugin(), p);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			if (activePlayers.contains(event.getTarget())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("minetinker.modifiers.shadowdive.use")) return;

		ItemStack boots = player.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) return;
		if (!modManager.hasMod(boots, this)) return;

		if (event.isSneaking()) { //enable
			Location loc = player.getLocation();
			byte lightlevel = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getLightLevel();
			boolean combatTagged = PlayerInfo.isCombatTagged(player);
			ChatWriter.logModifier(player, event, this, boots,
					String.format("LightLevel(%d/%d)", lightlevel, this.requiredLightLevel),
					String.format("InCombat(%b)", combatTagged));
			if (lightlevel > this.requiredLightLevel || player.hasPotionEffect(PotionEffectType.GLOWING)) {
				ChatWriter.sendActionBar(player, ChatColor.RED + this.getName() + ": "
						+ LanguageManager.getString("Modifier.Shadow-Dive.LightToHigh", player));
				return;
			}

			if (combatTagged) {
				ChatWriter.sendActionBar(player, ChatColor.RED + this.getName() + ": "
						+ LanguageManager.getString("Modifier.Shadow-Dive.InCombat", player));
				return;
			}

			hidePlayer(player);

		} else { //disable
			if (!activePlayers.contains(player)) return;
			showPlayer(player);
		}
	}
}

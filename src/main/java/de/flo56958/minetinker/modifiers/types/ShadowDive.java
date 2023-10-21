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
import java.util.concurrent.ConcurrentHashMap;

public class ShadowDive extends Modifier implements Listener {

	private static ShadowDive instance;

	private int requiredLightLevel;
	private final ConcurrentHashMap<Player, Integer> activePlayers = new ConcurrentHashMap<>();

	private BukkitTask task;
	private ShadowDive() {
		super(MineTinker.getPlugin());
		customModelData = 10_052;
	}

	public static ShadowDive instance() {
		synchronized (ShadowDive.class) {
			if (instance == null)
				instance = new ShadowDive();
		}

		return instance;
	}

	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Iterator<Player> iterator = activePlayers.keySet().iterator();
			while (iterator.hasNext()) {
				final Player p = iterator.next();
				final Location loc = p.getLocation();
				final int level = activePlayers.get(p);
				final byte lightlevel = p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getLightLevel();
				if (!p.isSneaking() || lightlevel > requiredLightLevel + level || p.hasPotionEffect(PotionEffectType.GLOWING)) {
					showPlayer(p);
					ChatWriter.sendActionBar(p, ChatColor.RED + ShadowDive.instance().getName() + ": "
							+ LanguageManager.getString("Modifier.Shadow-Dive.LightToHigh", p));
					continue;
				} else if (PlayerInfo.isCombatTagged(p)) {
					showPlayer(p);
					ChatWriter.sendActionBar(p, ChatColor.RED + ShadowDive.instance().getName() + ": "
							+ LanguageManager.getString("Modifier.Shadow-Dive.InCombat", p));
					continue;
				}
				ChatWriter.sendActionBar(p, ChatColor.MAGIC + "56958" + ChatColor.RESET
						+ ChatColor.GRAY + LanguageManager.getString("Modifier.Shadow-Dive.Active", p)
						+ ChatColor.RESET + ChatColor.MAGIC + "56958");
				hidePlayer(p, level);
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
		config.addDefault("MaxLevel", 10);
		config.addDefault("SlotCost", 3);
		config.addDefault("RequiredLightLevel", 3);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

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
		this.requiredLightLevel = config.getInt("RequiredLightLevel", 3);

		this.description = this.description.replaceAll("%level", String.valueOf(this.requiredLightLevel));

		if (this.isAllowed()) task = Bukkit.getScheduler().runTaskTimer(MineTinker.getPlugin(), runnable, 0,5);
	}

	private void hidePlayer(Player p, int level) {
		activePlayers.put(p, level);

		//Clear all mob targets
		p.getWorld().getNearbyEntities(p.getLocation(), 64, 64, 64).stream()
			.filter(entity -> entity instanceof Creature)
			.filter(entity -> p.equals(((Creature) entity).getTarget()))
			.forEach(entity -> ((Creature) entity).setTarget(null));

		//Hide from all players
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
				player.showPlayer(MineTinker.getPlugin(), p);
			else
				player.hidePlayer(MineTinker.getPlugin(), p);
		});
	}

	private void showPlayer(Player p) {
		if (activePlayers.remove(p) != null) {
			ChatWriter.sendActionBar(p, "");
			Bukkit.getServer().getOnlinePlayers().forEach(player -> player.showPlayer(MineTinker.getPlugin(), p));
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		for(final Player p : activePlayers.keySet()) {
			event.getPlayer().hidePlayer(MineTinker.getPlugin(), p);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onLeave(PlayerQuitEvent event) {
		for(final Player p : activePlayers.keySet()) {
			event.getPlayer().showPlayer(MineTinker.getPlugin(), p);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player) {
			//noinspection SuspiciousMethodCalls
			if (activePlayers.containsKey(event.getTarget())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSneak(PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack boots = player.getInventory().getBoots();
		if (!modManager.isArmorViable(boots)) return;
		if (!modManager.hasMod(boots, this)) return;
		final int level = modManager.getModLevel(boots, this);

		if (event.isSneaking() && !player.isGliding() && !player.isFlying()) { //enable
			Location loc = player.getLocation();
			byte lightlevel = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getLightLevel();
			boolean combatTagged = PlayerInfo.isCombatTagged(player);
			ChatWriter.logModifier(player, event, this, boots,
					String.format("LightLevel(%d/%d)", lightlevel, this.requiredLightLevel + level),
					String.format("InCombat(%b)", combatTagged));
			if (lightlevel > this.requiredLightLevel + level || player.hasPotionEffect(PotionEffectType.GLOWING)) {
				ChatWriter.sendActionBar(player, ChatColor.RED + this.getName() + ": "
						+ LanguageManager.getString("Modifier.Shadow-Dive.LightToHigh", player));
				return;
			}

			if (combatTagged) {
				ChatWriter.sendActionBar(player, ChatColor.RED + this.getName() + ": "
						+ LanguageManager.getString("Modifier.Shadow-Dive.InCombat", player));
				return;
			}

			hidePlayer(player, modManager.getModLevel(boots, this));
		} else { //disable
			if (!activePlayers.containsKey(player)) return;
			showPlayer(player);
		}
	}
}

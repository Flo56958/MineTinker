package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.api.events.MTProjectileHitEvent;
import de.flo56958.minetinker.api.events.MTProjectileLaunchEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.PlayerConfigurableModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import de.flo56958.minetinker.utils.data.DataHandler;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationManager;
import de.flo56958.minetinker.utils.playerconfig.PlayerConfigurationOption;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class Ender extends PlayerConfigurableModifier implements Listener {

	private static Ender instance;
	private boolean hasSound;
	private boolean hasParticles;
	private boolean giveNauseaOnUse;
	private int nauseaDuration;
	private boolean giveBlindnessOnUse;
	private int blindnessDuration;

	private Ender() {
		super(MineTinker.getPlugin());
		customModelData = 10_009;
	}

	public static Ender instance() {
		synchronized (Ender.class) {
			if (instance == null)
				instance = new Ender();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Ender";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW, ToolType.TRIDENT);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 2);
		config.addDefault("SlotCost", 2);
		config.addDefault("ModifierItemMaterial", Material.ENDER_EYE.name());
		config.addDefault("Sound", true); // Enderman-Teleport-Sound
		config.addDefault("Particles", true);
		config.addDefault("GiveNauseaOnUse", true);
		config.addDefault("NauseaDuration", 5); // seconds
		config.addDefault("GiveBlindnessOnUse", true);
		config.addDefault("BlindnessDuration", 3); // seconds

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);
		config.addDefault("RequireSneaking", true);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "PPP");
		config.addDefault("Recipe.Middle", "PEP");
		config.addDefault("Recipe.Bottom", "PPP");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("P", Material.ENDER_PEARL.name());
		recipeMaterials.put("E", Material.ENDER_EYE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.hasSound = config.getBoolean("Sound", true);
		this.hasParticles = config.getBoolean("Particles", true);
		this.giveNauseaOnUse = config.getBoolean("GiveNauseaOnUse", true);
		this.nauseaDuration = config.getInt("NauseaDuration", 5) * 20;
		this.giveBlindnessOnUse = config.getBoolean("GiveBlindnessOnUse", true);
		this.blindnessDuration = config.getInt("BlindnessDuration", 3) * 20;
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(EntityShootBowEvent event) {
		Entity arrow = event.getProjectile();
		if (!(arrow instanceof Arrow)) return;
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack tool = event.getBow();
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;
		if (PlayerConfigurationManager.getInstance().getBoolean(player, REQUIRE_SNEAKING) && !player.isSneaking()) return;

		arrow.setMetadata(this.getKey(),
				new FixedMetadataValue(this.getSource(), 0));
	}

	// for tridents
	@EventHandler(ignoreCancelled = true)
	public void effect(final MTProjectileLaunchEvent event) {
		final Projectile projectile = event.getEvent().getEntity();
		if (!(projectile instanceof Trident trident)) return;

		final Player player = event.getPlayer();
		final ItemStack tool = trident.getItem();

		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;
		if (PlayerConfigurationManager.getInstance().getBoolean(player, REQUIRE_SNEAKING) && !player.isSneaking()) return;

		trident.setMetadata(this.getKey(),
				new FixedMetadataValue(this.getSource(), 0));
	}

	/**
	 * The Effect for the ProjectileHitEvent
	 *
	 * @param event the Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(MTProjectileHitEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getTool();
		final Projectile arrow = event.getEvent().getEntity();
		final List<MetadataValue> activated = arrow.getMetadata(this.getKey());
		if (activated.isEmpty()) return;
		if (!player.hasPermission(getUsePermission())) return;

		final Location loc = event.getEvent().getEntity().getLocation().clone(); //Location of the Arrow
		final Location oldLoc = player.getLocation();

		player.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(),
				player.getLocation().getYaw(), player.getLocation().getPitch()).add(0, 1, 0));

		if (this.hasSound)
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);

		//Track stats
		final int stat = DataHandler.getTagOrDefault(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		DataHandler.setTag(tool, getKey() + "_stat_used", stat + 1, PersistentDataType.INTEGER);

		double distance = DataHandler.getTagOrDefault(tool, getKey() + "_stat_distance", PersistentDataType.DOUBLE, 0.0D);
		distance += oldLoc.distance(loc);
		DataHandler.setTag(tool, getKey() + "_stat_distance", distance, PersistentDataType.DOUBLE);

		spawnParticles(player, oldLoc);

		if (this.giveNauseaOnUse) {
			player.removePotionEffect(PotionEffectType.NAUSEA);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, this.nauseaDuration, 0, false, false));
		}
		if (this.giveBlindnessOnUse) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
		}
		ChatWriter.logModifier(player, event, this, tool,
				String.format("Location: (%s: %d/%d/%d -> %d/%d/%d)",
						loc.getWorld().getName(),
						oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	/**
	 * The Effect for the EntityDamageByEntityEvent
	 *
	 * @param event the event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getEvent().getEntity();

		//The Damage cause was not the arrow and therefore Ender should not be triggered
		if (!event.getEvent().getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) return;
		if (player.equals(event.getEvent().getEntity())) return;
		if (!player.hasPermission(getUsePermission())) return;
		if (!(event.getEvent().getDamager() instanceof Arrow arrow)) return;

		final ItemStack tool = event.getTool();
		final List<MetadataValue> activated = arrow.getMetadata(this.getKey());
		if (activated.isEmpty()) return;

		if (modManager.getModLevel(tool, this) < 2) return; //Level 2 is required for this effect

		if (entity instanceof Player) {
			//Save Players from getting teleported
			if (entity.hasPermission("minetinker.modifiers.ender.prohibittp")) {
				ChatWriter.sendActionBar(player, LanguageManager.getString("Modifier.Ender.TeleportationProhibited", player));
				ChatWriter.logModifier(player, event, this, tool, "Teleport denied");
				return;
			}
		}

		final Location loc = entity.getLocation().clone();
		final Location oldLoc = player.getLocation();
		entity.teleport(oldLoc);

		spawnParticles(player, loc);

		player.teleport(loc);

		//Track stats
		int stat = DataHandler.getTagOrDefault(tool, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		DataHandler.setTag(tool, getKey() + "_stat_used", stat + 1, PersistentDataType.INTEGER);

		double distance = DataHandler.getTagOrDefault(tool, getKey() + "_stat_distance", PersistentDataType.DOUBLE, 0.0D);
		distance += oldLoc.distance(loc);
		DataHandler.setTag(tool, getKey() + "_stat_distance", distance, PersistentDataType.DOUBLE);

		int swapped = DataHandler.getTagOrDefault(tool, getKey() + "_stat_swapped", PersistentDataType.INTEGER, 0);
		DataHandler.setTag(tool, getKey() + "_stat_swapped", swapped + 1, PersistentDataType.INTEGER);

		if (this.hasSound) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Players position
			player.getWorld().playSound(event.getEvent().getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Entity's position
		}

		if (this.giveNauseaOnUse) {
			player.removePotionEffect(PotionEffectType.NAUSEA);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, this.nauseaDuration, 0, false, false));

			if (entity instanceof LivingEntity livent) {
				livent.removePotionEffect(PotionEffectType.NAUSEA);
				livent.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, this.nauseaDuration, 0, false, false));
			}
		}

		if (this.giveBlindnessOnUse) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));

			if (entity instanceof LivingEntity livent) {
				livent.removePotionEffect(PotionEffectType.BLINDNESS);
				livent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
			}
		}

		ChatWriter.logModifier(player, event, this, tool, "Entity(" + entity.getType() + ")",
				String.format("Location: (%s: %d/%d/%d <-> %d/%d/%d)",
						loc.getWorld().getName(),
						oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public List<String> getStatistics(ItemStack item) {
		final List<String> lore = new ArrayList<>();
		final int stat = DataHandler.getTagOrDefault(item, getKey() + "_stat_used", PersistentDataType.INTEGER, 0);
		final double distance = DataHandler.getTagOrDefault(item, getKey() + "_stat_distance", PersistentDataType.DOUBLE, 0.0D);
		final int swapped = DataHandler.getTagOrDefault(item, getKey() + "_stat_swapped", PersistentDataType.INTEGER, 0);
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Ender.Statistic_Used")
				.replaceAll("%amount", String.valueOf(stat)));
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Ender.Statistic_Distance")
				.replaceAll("%amount", String.format("%,.2f", distance)));
		lore.add(ChatColor.WHITE + LanguageManager.getString("Modifier.Ender.Statistic_Swapped")
				.replaceAll("%amount", String.valueOf(swapped)));
		return lore;
	}

	private void spawnParticles(Player player, Location oldLoc) {
		if (this.hasParticles) {
			AreaEffectCloud cloud = (AreaEffectCloud) player.getWorld().spawnEntity(player.getLocation(), EntityType.AREA_EFFECT_CLOUD);
			cloud.setVelocity(new Vector(0, 1, 0));
			cloud.setRadius(0.5f);
			cloud.setDuration(5);
			cloud.setColor(Color.GREEN);
			cloud.getLocation().setYaw(90);

			AreaEffectCloud cloud2 = (AreaEffectCloud) player.getWorld().spawnEntity(oldLoc, EntityType.AREA_EFFECT_CLOUD);
			cloud2.setVelocity(new Vector(0, 1, 0));
			cloud2.setRadius(0.5f);
			cloud2.setDuration(5);
			cloud2.setColor(Color.GREEN);
			cloud2.getLocation().setPitch(90);
		}
	}

	private final PlayerConfigurationOption REQUIRE_SNEAKING =
			new PlayerConfigurationOption(this, "require-sneaking", PlayerConfigurationOption.Type.BOOLEAN,
					LanguageManager.getString("Modifier.Ender.PCO_require_sneaking"), true);

	@Override
	public List<PlayerConfigurationOption> getPCIOptions() {
		return List.of(REQUIRE_SNEAKING);
	}
}

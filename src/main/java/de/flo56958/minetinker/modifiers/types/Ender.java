package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTProjectileHitEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import de.flo56958.minetinker.utils.LanguageManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ender extends Modifier implements Listener {

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
			if (instance == null) {
				instance = new Ender();
			}
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
		config.addDefault("Sound", true); //#Enderman-Teleport-Sound
		config.addDefault("Particles", true);
		config.addDefault("GiveNauseaOnUse", true);
		config.addDefault("NauseaDuration", 5); //seconds
		config.addDefault("GiveBlindnessOnUse", true);
		config.addDefault("BlindnessDuration", 3); //seconds

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

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

		init(Material.ENDER_EYE);

		this.hasSound = config.getBoolean("Sound", true);
		this.hasParticles = config.getBoolean("Particles", true);
		this.giveNauseaOnUse = config.getBoolean("GiveNauseaOnUse", true);
		this.nauseaDuration = config.getInt("NauseaDuration", 5) * 20;
		this.giveBlindnessOnUse = config.getBoolean("GiveBlindnessOnUse", true);
		this.blindnessDuration = config.getInt("BlindnessDuration", 3) * 20;
	}

	/**
	 * The Effect for the ProjectileHitEvent
	 *
	 * @param event the Event
	 */
	@EventHandler
	public void effect(MTProjectileHitEvent event) {
		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.ender.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (!player.isSneaking()) {
			return;
		}

		Location loc = event.getEvent().getEntity().getLocation().clone(); //Location of the Arrow
		Location oldLoc = player.getLocation();

		player.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()).add(0, 1, 0));

		if (this.hasSound) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F);
		}

		spawnParticles(player, oldLoc);

		if (this.giveNauseaOnUse) {
			player.removePotionEffect(PotionEffectType.CONFUSION);
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
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
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getEvent().getEntity();

		if (!player.isSneaking()) {
			return;
		}

		//The Damage cause was not the arrow and therefore Ender should not be triggered
		if (!event.getEvent().getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			return;
		}

		if (player.equals(event.getEvent().getEntity())) {
			return;
		}

		if (!player.hasPermission("minetinker.modifiers.ender.use")) {
			return;
		}

		ItemStack tool = event.getTool();

		if (!modManager.hasMod(tool, this)) {
			return; //No check needed, as Ender can only be applied on the Bow
		}

		if (modManager.getModLevel(tool, this) < 2) {
			return;
		}

		if (entity instanceof Player) {
			//Save Players from getting teleported
			if (entity.hasPermission("minetinker.modifiers.ender.prohibittp")) {
				ChatWriter.sendActionBar(player, LanguageManager.getString("Modifier.Ender.TeleportationProhibited", player));
				ChatWriter.logModifier(player, event, this, tool, "Teleport denied");
				return;
			}
		}

		Location loc = entity.getLocation().clone();
		Location oldLoc = player.getLocation();
		entity.teleport(oldLoc);

		spawnParticles(player, loc);

		player.teleport(loc);

		if (this.hasSound) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Players position
			player.getWorld().playSound(event.getEvent().getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.3F); //Sound at Entity's position
		}

		if (this.giveNauseaOnUse) {
			player.removePotionEffect(PotionEffectType.CONFUSION);
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));

			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).removePotionEffect(PotionEffectType.CONFUSION);
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, this.nauseaDuration, 0, false, false));
			}
		}

		if (this.giveBlindnessOnUse) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));

			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).removePotionEffect(PotionEffectType.BLINDNESS);
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.blindnessDuration, 0, false, false));
			}
		}

		ChatWriter.logModifier(player, event, this, tool, "Entity(" + entity.getType().toString() + ")",
				String.format("Location: (%s: %d/%d/%d <-> %d/%d/%d)",
						loc.getWorld().getName(),
						oldLoc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
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
}

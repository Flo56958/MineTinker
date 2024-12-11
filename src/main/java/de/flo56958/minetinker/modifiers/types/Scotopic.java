package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.CooldownModifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scotopic extends CooldownModifier implements Listener {

	private static Scotopic instance;

	private int requiredLightLevel;
	private int durationPerLevel;
	private boolean givesImmunity;

	private Scotopic() {
		super(MineTinker.getPlugin());
		customModelData = 10_053;
	}

	public static Scotopic instance() {
		synchronized (Scotopic.class) {
			if (instance == null)
				instance = new Scotopic();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Scotopic";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Collections.singletonList(ToolType.HELMET);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%YELLOW%");
		config.addDefault("MaxLevel", 3);
		config.addDefault("SlotCost", 2);
		config.addDefault("ModifierItemMaterial", Material.FERMENTED_SPIDER_EYE.name());
		config.addDefault("RequiredLightLevel", 6);
		config.addDefault("CooldownInSeconds", 120.0); //in seconds
		config.addDefault("DurationPerLevel", 100); //in ticks
		config.addDefault("CooldownReductionPerLevel", 0.65);
		config.addDefault("GivesImmunityToBlindness", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", "S S");
		config.addDefault("Recipe.Middle", " F ");
		config.addDefault("Recipe.Bottom", "S S");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("S", Material.SPIDER_EYE.name());
		recipeMaterials.put("F", Material.FERMENTED_SPIDER_EYE.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.requiredLightLevel = config.getInt("RequiredLightLevel", 6);
		this.durationPerLevel = config.getInt("DurationPerLevel", 100);
		this.cooldownInSeconds = config.getDouble("CooldownInSeconds", 120.0);
		this.cooldownReductionPerLevel = config.getDouble("CooldownReductionPerLevel", 0.65);
		this.givesImmunity = config.getBoolean("GivesImmunityToEffect", true);

		this.description = this.description
				.replaceAll("%amount", String.valueOf(this.durationPerLevel / 20.0))
				.replaceAll("%light", String.valueOf(this.requiredLightLevel))
				.replaceAll("%cmax", String.valueOf(this.cooldownInSeconds))
				.replaceAll("%cmin", String.valueOf(this.getCooldown(this.getMaxLvl()) / 1000.0));
	}

	@EventHandler(ignoreCancelled = true)
	public void onMoveImmune(PlayerMoveEvent event) {
		if (!this.givesImmunity) return;

		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack armor = player.getInventory().getHelmet();
		if (armor == null) return;

		if (!modManager.hasMod(armor, this)) return;
		if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			ChatWriter.logModifier(player, event, this, armor, "RemoveBlindness");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission(getUsePermission())) return;

		final ItemStack helmet = player.getInventory().getHelmet();
		if (!modManager.isArmorViable(helmet)) return;
		int level = modManager.getModLevel(helmet, this);
		if (level <= 0) return;

		if (onCooldown(player, helmet, false, event)) return;

		final Location loc = event.getTo();
		if (loc == null) return;
		byte lightlevel = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getLightLevel();

		if (lightlevel > this.requiredLightLevel) return;

		final int duration = this.durationPerLevel * level;
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
				duration + 10 * 20, // To disable the flickering below 10 seconds
				level - 1, false, false, false));
		setCooldown(player, helmet);
		ChatWriter.logModifier(player, event, this, helmet,
				String.format("Cooldown(%ds)", getCooldown(level) / 1000),
				String.format("LightLevel(%d/%d)", lightlevel, this.requiredLightLevel),
				String.format("Duration(%d)", duration));

		Bukkit.getServer().getScheduler().runTaskLater(this.getSource(), () -> {
			PotionEffect effect = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
			if (effect == null) return;
			if (Math.abs(effect.getDuration() - duration) <= 1) {
				//Effect was most likely applied by MineTinker, so remove it
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
		}, duration);
	}
}

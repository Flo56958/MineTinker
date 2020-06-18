package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.events.MTProjectileHitEvent;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Shrouded extends Modifier implements Listener {

	private static Shrouded instance;
	private int duration;
	private double durationMultiplier;
	private double radiusPerLevel;

	private Shrouded() {
		super(MineTinker.getPlugin());
		customModelData = 10_054;
	}

	public static Shrouded instance() {
		synchronized (Shrouded.class) {
			if (instance == null) {
				instance = new Shrouded();
			}
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Shrouded";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.BOW, ToolType.CROSSBOW);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GREEN%");
		config.addDefault("MaxLevel", 2);
		config.addDefault("RadiusPerLevel", 1.5);
		config.addDefault("SlotCost", 1);
		config.addDefault("Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", " D ");
		config.addDefault("Recipe.Middle", "DTD");
		config.addDefault("Recipe.Bottom", " D ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("T", Material.TNT.name());
		recipeMaterials.put("D", Material.DRAGON_BREATH.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.getInstance().saveConfig(config);
		ConfigurationManager.getInstance().loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.DRAGON_BREATH);

		this.duration = config.getInt("Duration", 120);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
		this.radiusPerLevel = config.getDouble("RadiusPerLevel", 1.0);

		this.description = this.description.replace("%durationmin", String.valueOf(this.duration / 20))
				.replace("%durationmax", String.valueOf(Math.round(this.duration * Math.pow(this.durationMultiplier, getMaxLvl() - 1) * 5) / 100.0));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		effect(event, event.getPlayer(), event.getTool(), event.getEntity().getLocation());
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTProjectileHitEvent event) {
		effect(event, event.getPlayer(), event.getTool(), event.getEvent().getEntity().getLocation());
	}

	private void effect(Event event, Player player, ItemStack tool, Location location) {
		if (!player.hasPermission("minetinker.modifiers.shrouded.use")) {
			return;
		}
		if (!modManager.hasMod(tool, this)) {
			return;
		}

		int level = modManager.getModLevel(tool, this);

		AreaEffectCloud cloud = (AreaEffectCloud) location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);

		int duration = (int) Math.round(this.duration * Math.pow(this.durationMultiplier, level));
		float radius = (float) (this.radiusPerLevel * level);

		cloud.clearCustomEffects();
		cloud.setColor(Color.BLACK);
		cloud.setRadius(radius);
		cloud.setDuration(duration);

		cloud.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false), true);
		ArrayList<String> extras = new ArrayList<>();
		if (modManager.hasMod(tool, Glowing.instance()) && player.hasPermission("minetinker.modifiers.glowing.use")) {
			extras.add(Glowing.instance().getKey());
			cloud.addCustomEffect(Glowing.instance().getPotionEffect(null, null, player, tool), true);
		}
		if (modManager.hasMod(tool, Poisonous.instance()) && player.hasPermission("minetinker.modifiers.poisonous.use")) {
			extras.add(Poisonous.instance().getKey());
			cloud.addCustomEffect(Poisonous.instance().getPotionEffect(null, null, player, tool), true);
		}
		if (modManager.hasMod(tool, Shulking.instance()) && player.hasPermission("minetinker.modifiers.shulking.use")) {
			extras.add(Shulking.instance().getKey());
			cloud.addCustomEffect(Shulking.instance().getPotionEffect(null, null, player, tool), true);
		}
		if (modManager.hasMod(tool, Webbed.instance()) && player.hasPermission("minetinker.modifiers.webbed.use")) {
			extras.add(Webbed.instance().getKey());
			cloud.addCustomEffect(Webbed.instance().getPotionEffect(null, null, player, tool), true);
		}
		if (modManager.hasMod(tool, Withered.instance()) && player.hasPermission("minetinker.modifiers.withered.use")) {
			extras.add(Withered.instance().getKey());
			cloud.addCustomEffect(Withered.instance().getPotionEffect(null, null, player, tool), true);
		}
		StringBuilder ex = new StringBuilder();
		for (String s : extras) {
			ex.append(s).append("/");
		}
		if (!extras.isEmpty()) ex.deleteCharAt(ex.length() - 1);
		ChatWriter.logModifier(player, event, this, tool,
				String.format("Radius(%.2f)", radius),
				"Duration(" + duration + ")",
				"Extras(" + ex.toString() + ")");
	}
}

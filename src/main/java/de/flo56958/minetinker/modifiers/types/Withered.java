package de.flo56958.minetinker.modifiers.types;

import de.flo56958.minetinker.MineTinker;
import de.flo56958.minetinker.api.events.MTEntityDamageByEntityEvent;
import de.flo56958.minetinker.data.ToolType;
import de.flo56958.minetinker.modifiers.Modifier;
import de.flo56958.minetinker.utils.ChatWriter;
import de.flo56958.minetinker.utils.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Withered extends Modifier implements Listener {

	private static Withered instance;
	private int duration;
	private double durationMultiplier;
	private int effectAmplifier;
	private boolean effectHealsPlayer;

	private Withered() {
		super(MineTinker.getPlugin());
		customModelData = 10_055;
	}

	public static Withered instance() {
		synchronized (Withered.class) {
			if (instance == null)
				instance = new Withered();
		}

		return instance;
	}

	@Override
	public String getKey() {
		return "Withered";
	}

	@Override
	public List<ToolType> getAllowedTools() {
		return Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.SWORD, ToolType.TRIDENT,
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA, ToolType.MACE);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("ModifierItemMaterial", Material.WITHER_SKELETON_SKULL.name());
		config.addDefault("Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
		config.addDefault("EffectAmplifier", 1); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
		config.addDefault("EffectHealsPlayer", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);
		config.addDefault("MinimumToolLevelRequirement", 1);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", " W ");
		config.addDefault("Recipe.Middle", "WNW");
		config.addDefault("Recipe.Bottom", " W ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("W", Material.WITHER_SKELETON_SKULL.name());
		recipeMaterials.put("N", Material.NETHER_STAR.name());

		config.addDefault("Recipe.Materials", recipeMaterials);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init();

		this.duration = config.getInt("Duration", 120);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);
		this.effectHealsPlayer = config.getBoolean("EffectHealsPlayer", true);

		this.description = this.description.replace("%duration", String.valueOf(this.duration))
				.replace("%multiplier", String.valueOf(this.durationMultiplier));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();
		if (!player.hasPermission(getUsePermission())) return;
		if (!modManager.hasMod(tool, this)) return;
		if (modManager.hasMod(tool, Shrouded.instance())) return; //Should not trigger twice

		entity.addPotionEffect(getPotionEffect(event, event.getEntity(), player, tool));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.WITHER) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!this.effectHealsPlayer) return;

		if (!player.hasPermission(getUsePermission())) return;

		boolean hasWither = false;
		ItemStack armor = null;
		for (ItemStack stack : player.getInventory().getArmorContents()) {
			if (stack == null) continue;
			if (modManager.hasMod(stack, this)) {
				hasWither = true;
				armor = stack;
				break;
			}
		}

		if (!hasWither) return;

		double damage = event.getDamage();
		if (damage > 0) {
			event.setDamage(0);
			double health = player.getHealth();
			player.setHealth(Math.min(health + damage, player.getAttribute(Attribute.MAX_HEALTH).getValue()));
			ChatWriter.logModifier(player, event, this, armor, String.format("Health(%.2f -> %.2f)", health, player.getHealth()));
		}
	}

	public PotionEffect getPotionEffect(@Nullable Event event, @Nullable Entity entity, @NotNull Player player, @NotNull ItemStack tool) {
		int level = modManager.getModLevel(tool, this);
		int duration = (int) (this.duration * Math.pow(this.durationMultiplier, (level - 1)));
		int amplifier = this.effectAmplifier * (level - 1);
		if (entity == null) {
			ChatWriter.logModifier(player, event, this, tool,
					"Duration(" + duration + ")",
					"Amplifier(" + amplifier + ")");
		} else {
			ChatWriter.logModifier(player, event, this, tool,
					"Duration(" + duration + ")",
					"Amplifier(" + amplifier + ")",
					"Entity(" + entity.getType() + ")");
		}

		return new PotionEffect(PotionEffectType.WITHER, duration, amplifier, false, false);
	}
}

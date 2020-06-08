package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.MTEntityDamageByEntityEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
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
		super(Main.getPlugin());
		customModelData = 10_053;
	}

	public static Withered instance() {
		synchronized (Withered.class) {
			if (instance == null) {
				instance = new Withered();
			}
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
				ToolType.HELMET, ToolType.CHESTPLATE, ToolType.LEGGINGS, ToolType.BOOTS, ToolType.ELYTRA);
	}

	@Override
	public void reload() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);

		config.addDefault("Allowed", true);
		config.addDefault("Name", "Withered");
		config.addDefault("ModifierItemName", "Withered Wither Skeleton Skull");
		config.addDefault("Description", "Wither enemies!");
		config.addDefault("DescriptionModifierItem", "%WHITE%Modifier-Item for the Withered-Modifier");
		config.addDefault("Color", "%DARK_GRAY%");
		config.addDefault("MaxLevel", 5);
		config.addDefault("SlotCost", 1);
		config.addDefault("Duration", 120); //ticks INTEGER (20 ticks ~ 1 sec)
		config.addDefault("DurationMultiplier", 1.1); //Duration * (Multiplier^Level) DOUBLE
		config.addDefault("EffectAmplifier", 2); //per Level (Level 1 = 0, Level 2 = 2, Level 3 = 4, ...) INTEGER
		config.addDefault("EffectHealsPlayer", true);

		config.addDefault("EnchantCost", 10);
		config.addDefault("Enchantable", false);

		config.addDefault("Recipe.Enabled", true);
		config.addDefault("Recipe.Top", " W ");
		config.addDefault("Recipe.Middle", "WNW");
		config.addDefault("Recipe.Bottom", " W ");

		Map<String, String> recipeMaterials = new HashMap<>();
		recipeMaterials.put("W", Material.WITHER_SKELETON_SKULL.name());
		recipeMaterials.put("N", Material.NETHER_STAR.name());

		config.addDefault("Recipe.Materials", recipeMaterials);
		config.addDefault("OverrideLanguagesystem", false);

		ConfigurationManager.saveConfig(config);
		ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

		init(Material.WITHER_SKELETON_SKULL);

		this.duration = config.getInt("Duration", 120);
		this.durationMultiplier = config.getDouble("DurationMultiplier", 1.1);
		this.effectAmplifier = config.getInt("EffectAmplifier", 2);
		this.effectHealsPlayer = config.getBoolean("EffectHealsPlayer", true);

		this.description = this.description.replace("%duration", String.valueOf(this.duration))
				.replace("%multiplier", String.valueOf(this.durationMultiplier));
	}

	@EventHandler(ignoreCancelled = true)
	public void effect(MTEntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		Player player = event.getPlayer();
		ItemStack tool = event.getTool();

		if (!player.hasPermission("minetinker.modifiers.withered.use")) {
			return;
		}

		if (!modManager.hasMod(tool, this)) {
			return;
		}

		if (modManager.hasMod(tool, Shrouded.instance())) { //Should not trigger twice
			return;
		}

		((LivingEntity) event.getEntity()).addPotionEffect(getPotionEffect(event, event.getEntity(), player, tool));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.WITHER) return;
		if (!(event.getEntity() instanceof Player)) return;
		if (!this.effectHealsPlayer) return;

		Player player = (Player) event.getEntity();
		if (!player.hasPermission("minetinker.modifiers.withered.use")) {
			return;
		}

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
			player.setHealth(Math.min(health + damage, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
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
					"Entity(" + entity.getType().toString() + ")");
		}

		return new PotionEffect(PotionEffectType.WITHER, duration, amplifier, false, false);
	}
}
